package com.envanter.inventory.service;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.exception.ConflictException;
import com.envanter.inventory.exception.ResourceNotFoundException;
import com.envanter.inventory.exception.ValidationException;
import com.envanter.inventory.factory.StockValidationStrategyFactory;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.MovementType;
import com.envanter.inventory.model.StockLog;
import com.envanter.inventory.model.StockMovement;
import com.envanter.inventory.repository.JdbcItemRepository;
import com.envanter.inventory.repository.StockMovementRepository;
import com.envanter.inventory.repository.MongoStockLogRepository;
import com.envanter.inventory.strategy.CompositeStockValidationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StockMovementService implementasyonu — TDD GREEN asama.
 *
 * <p>Is akisi (createMovement):</p>
 * <ol>
 *   <li>Item varligini kontrol et (404)</li>
 *   <li>Request dogrulamasi (quantity > 0)</li>
 *   <li>StrategyFactory üzerinden CompositeStockValidationStrategy çalistir</li>
 *   <li>QuantityCheckStrategy false → 409 Conflict</li>
 *   <li>MinStockCheckStrategy false → hareket yapilir + notification trigger</li>
 *   <li>Stok miktarini guncelle (PostgreSQL)</li>
 *   <li>Hareketi kaydet (PostgreSQL)</li>
 *   <li>Log kaydet (MongoDB)</li>
 * </ol>
 */
@Service
public class StockMovementServiceImpl implements StockMovementService {

    private static final Logger log = LoggerFactory.getLogger(StockMovementServiceImpl.class);

    private final JdbcItemRepository             itemRepository;
    private final StockMovementRepository        movementRepository;
    private final MongoStockLogRepository        stockLogRepository;
    private final StockValidationStrategyFactory strategyFactory;
    private final NotificationClient             notificationClient;

    public StockMovementServiceImpl(
            JdbcItemRepository itemRepository,
            StockMovementRepository movementRepository,
            MongoStockLogRepository stockLogRepository,
            StockValidationStrategyFactory strategyFactory,
            NotificationClient notificationClient) {
        this.itemRepository     = itemRepository;
        this.movementRepository = movementRepository;
        this.stockLogRepository = stockLogRepository;
        this.strategyFactory    = strategyFactory;
        this.notificationClient = notificationClient;
    }

    // -------------------------------------------------------------------------
    // StockMovementService impl
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public StockMovementDTO createMovement(StockMovementRequest request) {
        // 1) Temel dogrulama
        if (request.getQuantity() <= 0) {
            throw new ValidationException("Hareket miktari sifirdan buyuk olmalidir.");
        }
        if (request.getMovementType() == null) {
            throw new ValidationException("Hareket tipi (IN/OUT) belirtilmelidir.");
        }

        // 2) Item var mi?
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item bulunamadi: id=" + request.getItemId()));

        // 3) Strategy dogrulamasi
        CompositeStockValidationStrategy composite = strategyFactory.createDefault();
        CompositeStockValidationStrategy.ValidationResult result =
                composite.validate(item, request);

        // QuantityCheckStrategy → stok yetersiz → 409 Conflict
        if (!result.passed()) {
            throw new ConflictException(result.errorMessage());
        }

        int quantityBefore = item.getQuantity();

        // 4) Stok miktarini guncelle
        if (request.getMovementType() == MovementType.IN) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item.setQuantity(item.getQuantity() - request.getQuantity());
        }
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);

        int quantityAfter = item.getQuantity();

        // 5) Hareketi PostgreSQL'e kaydet
        StockMovement movement = buildMovement(request);
        StockMovement saved = movementRepository.save(movement);
        log.info("Stok hareketi kaydedildi: id={}, type={}, qty={}",
                saved.getId(), saved.getMovementType(), saved.getQuantity());

        // 6) Log'u MongoDB'ye kaydet
        StockLog stockLog = StockLog.from(saved, item.getItemCode(), item.getName(),
                quantityBefore, quantityAfter);
        stockLogRepository.save(stockLog);

        // 7) MinStockCheckStrategy uyarisi → notification trigger
        if (result.warning()) {
            log.warn("Min stock altina dusuldu: itemId={}, qty={}, min={}",
                    item.getId(), quantityAfter, item.getMinStockLevel());
            notificationClient.sendLowStockAlert(
                    item.getId(), quantityAfter, item.getMinStockLevel());
        }

        return toDTO(saved, item, quantityAfter);
    }

    @Override
    public List<StockMovementDTO> getMovementsByItem(Long itemId) {
        // Item var mi kontrol et
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item bulunamadi: id=" + itemId));

        return movementRepository.findByItemId(itemId)
                .stream()
                .map(m -> toDTO(m, item, item.getQuantity()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDTO> getAllMovements() {
        return movementRepository.findAll()
                .stream()
                .map(m -> toDTOMinimal(m))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDTO> getMovementsByType(MovementType type) {
        return movementRepository.findByMovementType(type)
                .stream()
                .map(m -> toDTOMinimal(m))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDTO> getMovementsByDateRange(LocalDateTime from, LocalDateTime to) {
        return movementRepository.findByDateRange(from, to)
                .stream()
                .map(m -> toDTOMinimal(m))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearAllHistory() {
        log.warn("Tum stok hareket gecmisi ve loglar temizleniyor...");
        movementRepository.clearAll();
        stockLogRepository.clearAll();
        log.info("Temizlik islemi tamamlandi.");
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    /** Item detaysız hızlı mapping (listeleme endpoint'leri için). */
    private StockMovementDTO toDTOMinimal(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setItemId(movement.getItemId());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setReason(movement.getReason());
        dto.setUserId(movement.getUserId());
        dto.setMovementDate(movement.getMovementDate());
        dto.setReferenceNumber(movement.getReferenceNumber());
        dto.setAssignedTo(movement.getAssignedTo());
        dto.setReturnDate(movement.getReturnDate());

        // itemName'i doldur — Android adapter bunu gösteriyor
        itemRepository.findById(movement.getItemId()).ifPresent(item -> {
            dto.setItemName(item.getName());
            dto.setItemCode(item.getItemCode());
        });

        return dto;
    }

    private StockMovement buildMovement(StockMovementRequest request) {
        StockMovement m = new StockMovement();
        m.setItemId(request.getItemId());
        m.setMovementType(request.getMovementType());
        m.setQuantity(request.getQuantity());
        m.setReason(request.getReason());
        m.setUserId(request.getUserId());
        m.setReferenceNumber(request.getReferenceNumber());
        m.setAssignedTo(request.getAssignedTo());
        m.setReturnDate(request.getReturnDate());
        m.setMovementDate(LocalDateTime.now());
        return m;
    }

    private StockMovementDTO toDTO(StockMovement movement, Item item, int stockAfter) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setItemId(movement.getItemId());
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getName());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setStockAfterMovement(stockAfter);
        dto.setReason(movement.getReason());
        dto.setUserId(movement.getUserId());
        dto.setMovementDate(movement.getMovementDate());
        dto.setReferenceNumber(movement.getReferenceNumber());
        dto.setAssignedTo(movement.getAssignedTo());
        dto.setReturnDate(movement.getReturnDate());
        return dto;
    }
}
