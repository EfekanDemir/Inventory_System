package com.envanter.inventory.util;

import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
import org.springframework.stereotype.Component;

/**
 * SRP — Tek Sorumluluk: Item entity ↔ DTO dönüşümleri.
 *
 * <p>Mapping mantığı ItemServiceImpl'den tamamen ayrıştırıldı.
 * Yeni bir alan eklendiğinde yalnızca bu sınıf değişir.</p>
 */
@Component
public class ItemMapper {

    /**
     * ItemRequest → yeni Item entity dönüşümü.
     */
    public Item toEntity(ItemRequest request) {
        Item item = new Item();
        applyRequest(item, request);
        return item;
    }

    /**
     * Mevcut Item entity'sini request verileriyle günceller (in-place).
     */
    public void updateEntity(Item item, ItemRequest request) {
        applyRequest(item, request);
    }

    /**
     * Item entity → ItemDTO dönüşümü.
     */
    public ItemDTO toDTO(Item item) {
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

    // -- Private helper -------------------------------------------------------

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
