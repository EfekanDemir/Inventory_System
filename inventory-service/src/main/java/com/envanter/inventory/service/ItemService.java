package com.envanter.inventory.service;

import com.envanter.inventory.dto.ItemDTO;
import com.envanter.inventory.dto.ItemRequest;
import com.envanter.inventory.dto.StockReportDTO;

import java.util.List;

/**
 * ItemService arayuzu.
 */
public interface ItemService {

    ItemDTO createItem(ItemRequest request);

    ItemDTO getItemById(Long id);

    /** Tum itemlari doner. */
    List<ItemDTO> getAllItems();

    /** Kategori bazli itemlari doner. */
    List<ItemDTO> getItemsByCategory(Long categoryId);

    /** quantity <= minStockLevel olan itemlari doner. */
    List<ItemDTO> getLowStockItems();

    /** Ad veya itemCode'da arama yapar (case-insensitive). */
    List<ItemDTO> searchItems(String keyword);

    ItemDTO updateItem(Long id, ItemRequest request);

    /** Soft-delete: status = DISCONTINUED olarak isaretle. */
    void deleteItem(Long id);

    /** Stok toplam deger raporu. */
    StockReportDTO getStockReport();
}
