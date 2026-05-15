package com.envanter.android.model;

public class StockMovementRequest {
    private Long itemId;
    private String movementType; // "IN", "OUT"
    private int quantity;
    private String reason; // Backend expects reason instead of description
    private String assignedTo;
    private String returnDate;

    public StockMovementRequest(Long itemId, String movementType, int quantity, String reason) {
        this.itemId = itemId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
