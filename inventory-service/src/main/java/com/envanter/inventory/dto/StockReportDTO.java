package com.envanter.inventory.dto;

import java.math.BigDecimal;

/**
 * Stok durum raporu DTO'su.
 * GET /api/inventory/items/report endpoint'i için.
 */
public class StockReportDTO {

    private int totalItems;
    private int activeItems;
    private int lowStockItems;
    private int outOfStockItems;
    private BigDecimal totalStockValue;

    public StockReportDTO() {}

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public int getActiveItems() { return activeItems; }
    public void setActiveItems(int activeItems) { this.activeItems = activeItems; }

    public int getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(int lowStockItems) { this.lowStockItems = lowStockItems; }

    public int getOutOfStockItems() { return outOfStockItems; }
    public void setOutOfStockItems(int outOfStockItems) { this.outOfStockItems = outOfStockItems; }

    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }
}
