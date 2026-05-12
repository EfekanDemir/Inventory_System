package com.envanter.inventory.strategy;

import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.MovementType;
import org.springframework.stereotype.Component;

/**
 * Stok hareketi sonrasinda minimum stok seviyesinin altina dusulup
 * dusulmeyecegini kontrol eder.
 *
 * <p>Kural: Hareket sonrasi kalan stok minStockLevel'in altindaysa
 * bu strateji false doner ve bir bildirim tetiklenmelidir.
 * Giris (IN) hareketlerinde bu strateji her zaman true doner.</p>
 *
 * NOT: Bu strateji tek basina 409 atmaz; CompositeStockValidationStrategy
 * araciligiyla servise "uyari" olarak iletilir — servis katmani
 * notification trigger etme karari verir.
 */
@Component
public class MinStockCheckStrategy implements StockValidationStrategy {

    @Override
    public boolean validateMovement(Item item, StockMovementRequest request) {
        // IN hareketleri min stock seviyesini dusuremiyor
        if (request.getMovementType() != MovementType.OUT) {
            return true;
        }
        // Hareket sonrasi kalan stok hesaplanir
        int stockAfter = item.getQuantity() - request.getQuantity();
        // Min stock altina dusseydi bile hareket reddedilmez; sadece false doner
        // (servis katmani notification gonderip hareketi yine de uygular)
        return stockAfter >= item.getMinStockLevel();
    }

    @Override
    public String getErrorMessage(Item item, StockMovementRequest request) {
        int stockAfter = item.getQuantity() - request.getQuantity();
        return String.format(
                "Dusuk stok uyarisi: '%s' (kod: %s) hareketten sonra stok %d olacak, " +
                "minimum esik: %d. Bildirim gonderildi.",
                item.getName(),
                item.getItemCode(),
                stockAfter,
                item.getMinStockLevel()
        );
    }
}
