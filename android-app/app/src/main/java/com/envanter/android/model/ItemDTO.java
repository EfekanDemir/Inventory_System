package com.envanter.android.model;

public class ItemDTO {
    private Long id;
    private String itemCode;
    private String name;
    private int quantity;
    private int minStockLevel;
    private String itemStatus;

    public Long getId() { return id; }
    public String getItemCode() { return itemCode; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public String getItemStatus() { return itemStatus; }
}
