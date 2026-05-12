package com.envanter.inventory.dto;

import com.envanter.inventory.model.MovementType;

/**
 * Stok hareketi olusturma istek body'si.
 */
public class StockMovementRequest {

    private Long itemId;
    private MovementType movementType; // IN veya OUT
    private int quantity;
    private String reason;
    private Long userId;
    private String referenceNumber;

    public StockMovementRequest() {}

    public StockMovementRequest(Long itemId, MovementType movementType, int quantity) {
        this.itemId = itemId;
        this.movementType = movementType;
        this.quantity = quantity;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}
