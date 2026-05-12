package com.envanter.inventory.repository;

import com.envanter.inventory.model.StockMovement;

import java.util.List;
import java.util.Optional;

/**
 * StockMovement persistence icin repository arayuzu.
 * Implementasyonu: JdbcStockMovementRepository (Uve 1 tarafindan yazilacak).
 */
public interface StockMovementRepository {

    Optional<StockMovement> findById(Long id);

    List<StockMovement> findByItemId(Long itemId);

    StockMovement save(StockMovement movement);

    List<StockMovement> findAll();
}
