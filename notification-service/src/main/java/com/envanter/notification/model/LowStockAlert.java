package com.envanter.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Düşük stok uyarı kaydı — MongoDB "low_stock_alerts" koleksiyonuna persist edilir.
 *
 * <p>Stok min. eşiğin altına düştüğünde otomatik oluşturulur.
 * Stok yeniden doldurulunca alertStatus → RESOLVED yapılır.</p>
 *
 * İndeksler:
 * - itemId      → belirli ürünün aktif uyarılarını bulma
 * - alertStatus → tüm ACTIVE uyarıları listeleme
 * - createdAt   → tarih bazlı raporlama
 */
@Document(collection = "low_stock_alerts")
@CompoundIndex(name = "idx_item_status", def = "{'item_id': 1, 'alert_status': 1}")
public class LowStockAlert {

    @Id
    private String id;

    @Field("item_id")
    @Indexed
    private String itemId;

    @Field("item_name")
    private String itemName;

    @Field("current_stock")
    private int currentStock;

    @Field("min_stock_level")
    private int minStockLevel;

    /**
     * ACTIVE | RESOLVED
     */
    @Field("alert_status")
    @Indexed
    private String alertStatus;

    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;

    @Field("resolved_at")
    private LocalDateTime resolvedAt;

    // -- Constructors ---------------------------------------------------------

    public LowStockAlert() {}

    public LowStockAlert(String itemId, String itemName,
                         int currentStock, int minStockLevel) {
        this.itemId        = itemId;
        this.itemName      = itemName;
        this.currentStock  = currentStock;
        this.minStockLevel = minStockLevel;
        this.alertStatus   = "ACTIVE";
        this.createdAt     = LocalDateTime.now();
    }

    /** Uyarıyı çözüldü olarak işaretler. */
    public void resolve() {
        this.alertStatus = "RESOLVED";
        this.resolvedAt  = LocalDateTime.now();
    }

    // -- Getters & Setters ----------------------------------------------------

    public String getId()                            { return id; }
    public void setId(String id)                     { this.id = id; }

    public String getItemId()                        { return itemId; }
    public void setItemId(String itemId)             { this.itemId = itemId; }

    public String getItemName()                      { return itemName; }
    public void setItemName(String itemName)         { this.itemName = itemName; }

    public int getCurrentStock()                     { return currentStock; }
    public void setCurrentStock(int currentStock)    { this.currentStock = currentStock; }

    public int getMinStockLevel()                    { return minStockLevel; }
    public void setMinStockLevel(int min)            { this.minStockLevel = min; }

    public String getAlertStatus()                   { return alertStatus; }
    public void setAlertStatus(String alertStatus)   { this.alertStatus = alertStatus; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime t)        { this.createdAt = t; }

    public LocalDateTime getResolvedAt()             { return resolvedAt; }
    public void setResolvedAt(LocalDateTime t)       { this.resolvedAt = t; }
}
