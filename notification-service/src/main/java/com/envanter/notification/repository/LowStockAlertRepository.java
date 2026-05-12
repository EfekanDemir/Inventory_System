package com.envanter.notification.repository;

import com.envanter.notification.model.LowStockAlert;

import java.util.List;

/**
 * LowStockAlert persistence icin repository arayuzu.
 * MongoDB uzerinde calisir (MongoTemplate ile implemente edilecek).
 */
public interface LowStockAlertRepository {

    LowStockAlert save(LowStockAlert alert);

    List<LowStockAlert> findAll();

    List<LowStockAlert> findByItemId(String itemId);

    List<LowStockAlert> findByAlertStatus(String status); // ACTIVE | RESOLVED
}
