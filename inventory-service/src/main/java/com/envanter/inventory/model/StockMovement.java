package com.envanter.inventory.model;

import java.time.LocalDateTime;

/**
 * StockMovement entity — stok giris/cikis hareketi.
 * PostgreSQL STOCK_MOVEMENTS tablosuna karsilik gelir.
 */
public class StockMovement {

    private Long id;
    private Long itemId;
    private MovementType movementType;
    private int quantity;
    private String reason;
    private Long userId;
    private LocalDateTime movementDate;
    private String referenceNumber;

    public StockMovement() {}

    // -- Getters & Setters ----------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}
