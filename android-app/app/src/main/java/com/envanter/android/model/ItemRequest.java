package com.envanter.android.model;

public class ItemRequest {
    private String itemCode;
    private String name;
    private int quantity;
    private int minStockLevel;
    private Long categoryId;

    public ItemRequest(String itemCode, String name, int quantity, int minStockLevel, Long categoryId) {
        this.itemCode = itemCode;
        this.name = name;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.categoryId = categoryId;
    }
}
