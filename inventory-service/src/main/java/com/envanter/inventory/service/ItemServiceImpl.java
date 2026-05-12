package com.envanter.inventory.service;

import com.envanter.inventory.client.NotificationClient;
import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.exception.ConflictException;
import com.envanter.inventory.exception.ResourceNotFoundException;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
import com.envanter.inventory.repository.JdbcItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ItemService implementasyonu — TDD GREEN asama.
 *
 * <p>Sorumluluklari:</p>
 * <ul>
 *   <li>CRUD islemleri (JdbcItemRepository → PostgreSQL)</li>
 *   <li>itemCode benzersizligi kontrolu (409 Conflict)</li>
 *   <li>Dusuk stok tespitinde NotificationClient tetikleme</li>
 * </ul>
 */
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final JdbcItemRepository itemRepository;
    private final NotificationClient notificationClient;

    public ItemServiceImpl(JdbcItemRepository itemRepository,
                           NotificationClient notificationClient) {
        this.itemRepository     = itemRepository;
        this.notificationClient = notificationClient;
    }

    // -------------------------------------------------------------------------
    // ItemService impl
    // -------------------------------------------------------------------------

    @Override
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item bulunamadi: id=" + id));
        return toDTO(item);
    }

    @Override
    public ItemDTO createItem(ItemRequest request) {
        // itemCode benzersizligi — 409 Conflict
        if (itemRepository.findByItemCode(request.getItemCode()).isPresent()) {
            throw new ConflictException(
                    "Bu item kodu zaten mevcut: " + request.getItemCode());
        }

        Item item = fromRequest(request);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        Item saved = itemRepository.save(item);
        log.info("Yeni item olusturuldu: id={}, code={}", saved.getId(), saved.getItemCode());

        // Yeni item eklendi bildirimi
        notificationClient.sendInventoryAddedNotification(saved.getId(), saved.getName());

        // Ilk stok miktari min stock altindaysa uyar
        if (saved.getQuantity() <= saved.getMinStockLevel()) {
            log.warn("Yeni item min stock altinda: id={}, quantity={}, min={}",
                    saved.getId(), saved.getQuantity(), saved.getMinStockLevel());
            notificationClient.sendLowStockAlert(
                    saved.getId(), saved.getQuantity(), saved.getMinStockLevel());
        }

        return toDTO(saved);
    }

    @Override
    public ItemDTO updateItem(Long id, ItemRequest request) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guncellenecek item bulunamadi: id=" + id));

        // itemCode degistiyse yeni kodun baska kayita ait olmadigi kontrol edilir
        if (!existing.getItemCode().equals(request.getItemCode())) {
            itemRepository.findByItemCode(request.getItemCode()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new ConflictException(
                            "Bu item kodu baska bir kayita ait: " + request.getItemCode());
                }
            });
        }

        applyRequest(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());

        Item updated = itemRepository.save(existing);
        log.info("Item guncellendi: id={}", updated.getId());
        return toDTO(updated);
    }

    @Override
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silinecek item bulunamadi: id=" + id);
        }
        itemRepository.deleteById(id);
        log.info("Item silindi: id={}", id);
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setItemCode(item.getItemCode());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setCategoryId(item.getCategoryId());
        dto.setQuantity(item.getQuantity());
        dto.setMinStockLevel(item.getMinStockLevel());
        dto.setLocation(item.getLocation());
        dto.setStatus(item.getStatus());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }

    private Item fromRequest(ItemRequest request) {
        Item item = new Item();
        applyRequest(item, request);
        return item;
    }

    private void applyRequest(Item item, ItemRequest request) {
        item.setItemCode(request.getItemCode());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategoryId(request.getCategoryId());
        item.setQuantity(request.getQuantity());
        item.setMinStockLevel(request.getMinStockLevel());
        item.setLocation(request.getLocation());
        item.setStatus(request.getStatus() != null ? request.getStatus() : ItemStatus.ACTIVE);
        item.setUnitPrice(request.getUnitPrice());
    }
}
