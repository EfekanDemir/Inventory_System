package com.envanter.android.model;

public class StockMovementRequest {
    private Long itemId;
    private String movementType; // "IN", "OUT"
    private int quantity;
    private String description;

    public StockMovementRequest(Long itemId, String movementType, int quantity, String description) {
        this.itemId = itemId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.description = description;
    }
}
