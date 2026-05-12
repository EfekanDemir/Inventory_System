package com.envanter.inventory.repository;

import com.envanter.inventory.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Item persistence icin repository arayuzu.
 * Implementasyonu: JdbcItemRepository (Uve 1 tarafindan yazilacak).
 */
public interface ItemRepository {

    Optional<Item> findById(Long id);

    Optional<Item> findByItemCode(String itemCode);

    List<Item> findAll();

    List<Item> findByCategoryId(Long categoryId);

    /**
     * quantity <= minStockLevel olan kalemleri doner.
     */
    List<Item> findLowStockItems();

    Item save(Item item);

    void deleteById(Long id);

    boolean existsByItemCode(String itemCode);
}
