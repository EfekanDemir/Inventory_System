package com.envanter.android.model;

public class StockMovementDTO {
    private Long id;
    private Long itemId;
    private String movementType;
    private int quantity;
    private String timestamp;

    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public String getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public String getTimestamp() { return timestamp; }
}
