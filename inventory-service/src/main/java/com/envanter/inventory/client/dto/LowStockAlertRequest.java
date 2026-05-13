package com.envanter.inventory.client.dto;

/**
 * notification-service /api/notifications/low-stock endpoint'ine
 * gönderilen istek gövdesi.
 */
public class LowStockAlertRequest {

    private Long   itemId;
    private int    currentStock;
    private int    minStockLevel;

    public LowStockAlertRequest() {}

    public LowStockAlertRequest(Long itemId, int currentStock, int minStockLevel) {
        this.itemId        = itemId;
        this.currentStock  = currentStock;
        this.minStockLevel = minStockLevel;
    }

    public Long getItemId()          { return itemId; }
    public void setItemId(Long v)    { this.itemId = v; }

    public int  getCurrentStock()          { return currentStock; }
    public void setCurrentStock(int v)     { this.currentStock = v; }

    public int  getMinStockLevel()         { return minStockLevel; }
    public void setMinStockLevel(int v)    { this.minStockLevel = v; }
}
