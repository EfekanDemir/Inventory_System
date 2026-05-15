package com.envanter.inventory.service;

import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.model.MovementType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * StockMovementService arayuzu.
 */
public interface StockMovementService {

    StockMovementDTO createMovement(StockMovementRequest request);

    List<StockMovementDTO> getMovementsByItem(Long itemId);

    /** Tüm hareketleri listeler. */
    List<StockMovementDTO> getAllMovements();

    /** Hareket tipine göre filtreler (IN veya OUT). */
    List<StockMovementDTO> getMovementsByType(MovementType type);

    /** Tarih aralığına göre filtreler. */
    List<StockMovementDTO> getMovementsByDateRange(LocalDateTime from, LocalDateTime to);

    /** Tüm hareket geçmişini ve logları temizler. */
    void clearAllHistory();
}
