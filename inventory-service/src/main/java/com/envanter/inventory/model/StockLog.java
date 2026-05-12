package com.envanter.inventory.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * MongoDB stok hareket log dokumani.
 *
 * PostgreSQL'deki StockMovement kaydinin MongoDB'deki denetim izi kopyasi.
 * Koleksiyon: "stock_logs"
 *
 * Amac: hizli sorgulama, raporlama ve gecmis hareket analizi.
 */
@Document(collection = "stock_logs")
public class StockLog {

    @Id
    private String id;

    /** PostgreSQL'deki stock_movements.id referansi */
    @Field("movement_id")
    @Indexed
    private Long movementId;

    @Field("item_id")
    @Indexed
    private Long itemId;

    @Field("item_code")
    private String itemCode;

    @Field("item_name")
    private String itemName;

    /** IN veya OUT */
    @Field("movement_type")
    private String movementType;

    @Field("quantity")
    private int quantity;

    /** Hareketten once ki stok miktari */
    @Field("quantity_before")
    private int quantityBefore;

    /** Hareketten sonraki stok miktari */
    @Field("quantity_after")
    private int quantityAfter;

    @Field("reason")
    private String reason;

    @Field("reference_number")
    private String referenceNumber;

    @Field("user_id")
    private Long userId;

    @Field("movement_date")
    @Indexed
    private LocalDateTime movementDate;

    @Field("logged_at")
    private LocalDateTime loggedAt;

    // -- Constructors ---------------------------------------------------------

    public StockLog() {}

    /**
     * StockMovement'tan StockLog olusturan fabrika metodu.
     */
    public static StockLog from(StockMovement movement,
                                 String itemCode,
                                 String itemName,
                                 int quantityBefore,
                                 int quantityAfter) {
        StockLog log = new StockLog();
        log.movementId      = movement.getId();
        log.itemId          = movement.getItemId();
        log.itemCode        = itemCode;
        log.itemName        = itemName;
        log.movementType    = movement.getMovementType() != null
                ? movement.getMovementType().name() : null;
        log.quantity        = movement.getQuantity();
        log.quantityBefore  = quantityBefore;
        log.quantityAfter   = quantityAfter;
        log.reason          = movement.getReason();
        log.referenceNumber = movement.getReferenceNumber();
        log.userId          = movement.getUserId();
        log.movementDate    = movement.getMovementDate();
        log.loggedAt        = LocalDateTime.now();
        return log;
    }

    // -- Getters & Setters ----------------------------------------------------

    public String getId()                            { return id; }
    public void setId(String id)                     { this.id = id; }

    public Long getMovementId()                      { return movementId; }
    public void setMovementId(Long movementId)        { this.movementId = movementId; }

    public Long getItemId()                          { return itemId; }
    public void setItemId(Long itemId)               { this.itemId = itemId; }

    public String getItemCode()                      { return itemCode; }
    public void setItemCode(String itemCode)          { this.itemCode = itemCode; }

    public String getItemName()                      { return itemName; }
    public void setItemName(String itemName)          { this.itemName = itemName; }

    public String getMovementType()                  { return movementType; }
    public void setMovementType(String movementType)  { this.movementType = movementType; }

    public int getQuantity()                         { return quantity; }
    public void setQuantity(int quantity)             { this.quantity = quantity; }

    public int getQuantityBefore()                   { return quantityBefore; }
    public void setQuantityBefore(int quantityBefore) { this.quantityBefore = quantityBefore; }

    public int getQuantityAfter()                    { return quantityAfter; }
    public void setQuantityAfter(int quantityAfter)   { this.quantityAfter = quantityAfter; }

    public String getReason()                        { return reason; }
    public void setReason(String reason)              { this.reason = reason; }

    public String getReferenceNumber()               { return referenceNumber; }
    public void setReferenceNumber(String ref)        { this.referenceNumber = ref; }

    public Long getUserId()                          { return userId; }
    public void setUserId(Long userId)               { this.userId = userId; }

    public LocalDateTime getMovementDate()           { return movementDate; }
    public void setMovementDate(LocalDateTime d)      { this.movementDate = d; }

    public LocalDateTime getLoggedAt()               { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt)   { this.loggedAt = loggedAt; }
}
