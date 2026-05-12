package com.envanter.notification.model;

import java.time.LocalDateTime;

/**
 * Dusuk stok uyarisi kaydi -- MongoDB'ye @Document olarak persist edilir.
 * Stok min. esigi altina dusunce otomatik olusturulur.
 */
public class LowStockAlert {

    private String id; // MongoDB ObjectId
    private String itemId;
    private String itemName;
    private int currentStock;
    private int minStockLevel;
    private String alertStatus; // ACTIVE, RESOLVED
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public LowStockAlert() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getAlertStatus() { return alertStatus; }
    public void setAlertStatus(String alertStatus) { this.alertStatus = alertStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
