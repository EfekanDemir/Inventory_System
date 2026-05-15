package com.envanter.inventory.repository;

import com.envanter.common.generic.GenericRepository;
import com.envanter.inventory.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Item persistence icin repository arayuzu.
 * Implementasyonu: JdbcItemRepository.
 */
public interface ItemRepository extends GenericRepository<Item, Long> {

    Optional<Item> findByItemCode(String itemCode);

    List<Item> findByCategoryId(Long categoryId);

    /**
     * quantity <= minStockLevel olan kalemleri doner.
     */
    List<Item> findLowStockItems();

    Optional<Item> findByBarcode(String barcode);

    boolean existsByItemCode(String itemCode);

    List<Item> searchByKeyword(String keyword);

    void softDeleteById(Long id);
}
