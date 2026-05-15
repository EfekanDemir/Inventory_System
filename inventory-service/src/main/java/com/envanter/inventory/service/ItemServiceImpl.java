package com.envanter.inventory.service;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.dto.StockReportDTO;
import com.envanter.inventory.exception.ConflictException;
import com.envanter.inventory.exception.ResourceNotFoundException;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
import com.envanter.inventory.repository.ItemRepository;
import com.envanter.inventory.repository.CategoryRepository;
import com.envanter.inventory.util.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ItemService implementasyonu — SRP refactor sonrası.
 *
 * <p>Bu sınıfın TEK sorumluluğu: Item iş akışını orkestre etmek.</p>
 * <ul>
 *   <li>Mapping     → {@link ItemMapper}          (ayrı sınıf — SRP)</li>
 *   <li>Stok hareketi → {@link StockMovementService} (ayrı sınıf — önceden ayrı)</li>
 *   <li>Bildirim    → {@link NotificationClient}  (ayrı sınıf — önceden ayrı)</li>
 * </ul>
 *
 * SOLID: Constructor Injection zorunlu.
 */
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository  itemRepository;
    private final NotificationClient  notificationClient;
    private final ItemMapper          itemMapper;
    private final CategoryRepository  categoryRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           NotificationClient notificationClient,
                           ItemMapper itemMapper,
                           CategoryRepository categoryRepository) {
        this.itemRepository     = itemRepository;
        this.notificationClient = notificationClient;
        this.itemMapper         = itemMapper;
        this.categoryRepository = categoryRepository;
    }

    // -------------------------------------------------------------------------
    // ItemService impl
    // -------------------------------------------------------------------------

    @Override
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::toDTOWithCategory)
                .collect(Collectors.toList());
    }

    private ItemDTO toDTOWithCategory(Item item) {
        ItemDTO dto = itemMapper.toDTO(item);
        if (item.getCategoryId() != null) {
            categoryRepository.findById(item.getCategoryId())
                .ifPresentOrElse(
                    cat -> dto.setCategoryName(cat.getName()),
                    () -> dto.setCategoryName("Genel")
                );
        } else {
            dto.setCategoryName("Genel");
        }
        return dto;
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item bulunamadı: id=" + id));
        return toDTOWithCategory(item);
    }

    @Override
    public ItemDTO createItem(ItemRequest request) {
        // itemCode benzersizliği — 409 Conflict
        if (itemRepository.findByItemCode(request.getItemCode()).isPresent()) {
            throw new ConflictException(
                    "Bu item kodu zaten mevcut: " + request.getItemCode());
        }

        // SRP: mapping ItemMapper'a delege edildi
        Item item = itemMapper.toEntity(request);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        Item saved = itemRepository.save(item);
        log.info("Yeni item oluşturuldu: id={}, code={}", saved.getId(), saved.getItemCode());

        // SRP: bildirim NotificationClient'a delege edildi
        notificationClient.sendInventoryAddedNotification(saved.getId(), saved.getName());

        // Başlangıç stoku min eşiğin altındaysa uyar
        if (saved.getQuantity() <= saved.getMinStockLevel()) {
            log.warn("Yeni item min stock altında: id={}", saved.getId());
            notificationClient.sendLowStockAlert(
                    saved.getId(), saved.getQuantity(), saved.getMinStockLevel());
        }

        return toDTOWithCategory(saved);
    }

    @Override
    public ItemDTO updateItem(Long id, ItemRequest request) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Güncellenecek item bulunamadı: id=" + id));

        // itemCode değiştiyse yeni kodun başka kayıda ait olmadığı kontrol edilir
        if (!existing.getItemCode().equals(request.getItemCode())) {
            itemRepository.findByItemCode(request.getItemCode()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new ConflictException(
                            "Bu item kodu başka bir kayıda ait: " + request.getItemCode());
                }
            });
        }

        // SRP: güncelleme mapping ItemMapper'a delege edildi
        itemMapper.updateEntity(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());

        Item updated = itemRepository.save(existing);
        log.info("Item güncellendi: id={}", updated.getId());
        return toDTOWithCategory(updated);
    }

    @Override
    public List<ItemDTO> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toDTOWithCategory)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> getLowStockItems() {
        return itemRepository.findLowStockItems()
                .stream()
                .map(this::toDTOWithCategory)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> searchItems(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllItems();
        }
        return itemRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toDTOWithCategory)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silinecek item bulunamadı: id=" + id);
        }
        // Soft-delete: fiziksel silme yerine DISCONTINUED olarak isaretle
        itemRepository.softDeleteById(id);
        log.info("Item soft-delete edildi (DISCONTINUED): id={}", id);
    }

    @Override
    public StockReportDTO getStockReport() {
        List<Item> all = itemRepository.findAll();
        StockReportDTO report = new StockReportDTO();
        report.setTotalItems(all.size());
        report.setActiveItems((int) all.stream()
                .filter(i -> i.getStatus() == ItemStatus.ACTIVE).count());
        report.setLowStockItems((int) all.stream()
                .filter(i -> i.getQuantity() <= i.getMinStockLevel() && i.getStatus() == ItemStatus.ACTIVE).count());
        report.setOutOfStockItems((int) all.stream()
                .filter(i -> i.getQuantity() == 0 && i.getStatus() == ItemStatus.ACTIVE).count());
        BigDecimal totalValue = all.stream()
                .filter(i -> i.getStatus() == ItemStatus.ACTIVE && i.getUnitPrice() != null)
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalStockValue(totalValue);
        return report;
    }
}
