package com.envanter.mobile.model;

public class ItemStock {
    private String itemName;
    private int quantity;
    private int minStockLevel;

    public ItemStock(String itemName, int quantity, int minStockLevel) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
    }

    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getMinStockLevel() { return minStockLevel; }
}
