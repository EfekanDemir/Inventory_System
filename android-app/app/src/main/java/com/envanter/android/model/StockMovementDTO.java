package com.envanter.android.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StockMovementDTO {
    private Long id;
    private Long itemId;
    private String itemCode;
    private String itemName;
    
    @SerializedName("movementType")
    private String movementType; // "IN" veya "OUT"
    
    private int quantity;
    private String reason;
    private int stockAfterMovement;
    
    @SerializedName("movementDate")
    private Object movementDate; // Esnek tip (Array veya String gelebilir)

    @SerializedName("assignedTo")
    private String assignedTo;
    
    @SerializedName("returnDate")
    private Object returnDate;

    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public String getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public int getStockAfterMovement() { return stockAfterMovement; }
    public String getAssignedTo() { return assignedTo; }

    // Adapter için güvenli metodlar
    public String getType() { return movementType; }
    
    public String getCreatedAt() {
        return formatDate(movementDate);
    }
    
    public String getFormattedReturnDate() {
        if (returnDate == null) return "Belirtilmedi";
        return formatDate(returnDate);
    }

    private String formatDate(Object dateObj) {
        if (dateObj == null) return "Tarih Belirtilmedi";
        if (dateObj instanceof String) {
            String d = (String) dateObj;
            return d.contains("T") ? d.replace("T", " ").substring(0, 16) : d;
        }
        if (dateObj instanceof List) {
            List<?> list = (List<?>) dateObj;
            if (list.size() >= 5) {
                // [YYYY, MM, DD, HH, mm] formatı
                return list.get(2) + "/" + list.get(1) + "/" + list.get(0) + " " + list.get(3) + ":" + list.get(4);
            } else if (list.size() >= 3) {
                return list.get(2) + "/" + list.get(1) + "/" + list.get(0);
            }
        }
        return dateObj.toString();
    }
}
