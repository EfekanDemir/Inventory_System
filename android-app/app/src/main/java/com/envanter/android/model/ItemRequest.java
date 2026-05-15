package com.envanter.android.model;

public class ItemRequest {
    private String itemCode;
    private String name;
    private int quantity;
    private int minStockLevel;
    private Long categoryId;
    private String barcode;
    private java.math.BigDecimal unitPrice;

    public ItemRequest(String itemCode, String name, int quantity, int minStockLevel, Long categoryId) {
        this.itemCode = itemCode;
        this.name = name;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.categoryId = categoryId;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public java.math.BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
