package com.envanter.inventory.dto;

import com.envanter.inventory.model.ItemStatus;
import java.math.BigDecimal;

/**
 * Item olusturma/guncelleme istek body'si.
 */
public class ItemRequest {

    private String itemCode;
    private String name;
    private String description;
    private Long categoryId;
    private int quantity;
    private int minStockLevel;
    private String location;
    private ItemStatus status;
    private BigDecimal unitPrice;
    private String barcode;

    public ItemRequest() {}

    public ItemRequest(String itemCode, String name, Long categoryId,
                       int quantity, int minStockLevel) {
        this.itemCode = itemCode;
        this.name = name;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.status = ItemStatus.ACTIVE;
    }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
}
