package com.envanter.inventory.service;

import com.envanter.inventory.dto.StockMovementDTO;
import com.envanter.inventory.dto.StockMovementRequest;

import java.util.List;

/**
 * StockMovementService arayuzu.
 */
public interface StockMovementService {

    StockMovementDTO createMovement(StockMovementRequest request);

    List<StockMovementDTO> getMovementsByItem(Long itemId);
}
