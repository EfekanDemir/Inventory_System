package com.envanter.android.model;

import java.math.BigDecimal;

public class StockReportDTO {
    private long activeItems;
    private long lowStockItems;
    private BigDecimal totalStockValue;

    public long getActiveItems() { return activeItems; }
    public void setActiveItems(long activeItems) { this.activeItems = activeItems; }
    public long getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(long lowStockItems) { this.lowStockItems = lowStockItems; }
    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }
}
