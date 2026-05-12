package com.envanter.inventory.dto;

import com.envanter.inventory.model.MovementType;
import java.time.LocalDateTime;

/**
 * Stok hareketi veri transfer nesnesi — service katmanindan donar.
 */
public class StockMovementDTO {

    private Long id;
    private Long itemId;
    private String itemName;
    private String itemCode;
    private MovementType movementType;
    private int quantity;
    private int stockAfterMovement;
    private String reason;
    private Long userId;
    private LocalDateTime movementDate;
    private String referenceNumber;

    public StockMovementDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockAfterMovement() { return stockAfterMovement; }
    public void setStockAfterMovement(int stockAfterMovement) { this.stockAfterMovement = stockAfterMovement; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}
