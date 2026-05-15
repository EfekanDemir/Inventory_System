package com.envanter.inventory.repository;

import com.envanter.common.generic.GenericRepository;
import com.envanter.inventory.model.StockMovement;

import java.time.LocalDateTime;
import com.envanter.inventory.model.MovementType;
import java.util.List;

/**
 * StockMovement persistence icin repository arayuzu.
 * Implementasyonu: JdbcStockMovementRepository.
 */
public interface StockMovementRepository extends GenericRepository<StockMovement, Long> {

    List<StockMovement> findByItemId(Long itemId);

    List<StockMovement> findByMovementType(MovementType type);

    List<StockMovement> findByDateRange(LocalDateTime from, LocalDateTime to);

    /** Tüm hareketleri siler. */
    void clearAll();
}
