package com.envanter.inventory.client.dto;

/**
 * notification-service /api/notifications/inventory-added endpoint'ine
 * gönderilen istek gövdesi.
 */
public class InventoryAddedRequest {

    private Long   itemId;
    private String itemName;

    public InventoryAddedRequest() {}

    public InventoryAddedRequest(Long itemId, String itemName) {
        this.itemId    = itemId;
        this.itemName  = itemName;
    }

    public Long   getItemId()            { return itemId; }
    public void   setItemId(Long v)      { this.itemId = v; }

    public String getItemName()          { return itemName; }
    public void   setItemName(String v)  { this.itemName = v; }
}
