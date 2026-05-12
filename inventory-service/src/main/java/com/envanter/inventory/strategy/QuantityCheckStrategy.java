package com.envanter.inventory.strategy;

import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.MovementType;
import org.springframework.stereotype.Component;

/**
 * Stok cikis (OUT) hareketlerinde yeterli stok olup olmadigini kontrol eder.
 *
 * <p>Kural: Cikis miktari mevcut stok miktarindan buyukse hareket gecersizdir.
 * Giris (IN) hareketlerinde bu strateji her zaman true doner.</p>
 */
@Component
public class QuantityCheckStrategy implements StockValidationStrategy {

    @Override
    public boolean validateMovement(Item item, StockMovementRequest request) {
        // IN hareketlerini dogrudan gecir
        if (request.getMovementType() != MovementType.OUT) {
            return true;
        }
        // OUT hareketi: mevcut stok cikis miktarindan buyuk ya da esit olmali
        return item.getQuantity() >= request.getQuantity();
    }

    @Override
    public String getErrorMessage(Item item, StockMovementRequest request) {
        return String.format(
                "Yetersiz stok: '%s' (kod: %s) icin mevcut stok %d, talep edilen cikis %d.",
                item.getName(),
                item.getItemCode(),
                item.getQuantity(),
                request.getQuantity()
        );
    }
}
