package com.envanter.inventory.strategy;

import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.model.Item;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Birden fazla StockValidationStrategy'yi sirayla uygulayan birlestirici.
 *
 * <p>Open/Closed Principle: Yeni strateji eklemek icin bu sinifi degistirmek
 * gerekmez — sadece yeni bir StockValidationStrategy impl yazilir ve
 * factory'e eklenir.</p>
 *
 * Davraniş kurallari:
 * <ul>
 *   <li>QuantityCheckStrategy false → 409 Conflict firlatilir (stok yetersiz)</li>
 *   <li>MinStockCheckStrategy false → hareket yapilir ama notification tetiklenir</li>
 * </ul>
 */
@Component
public class CompositeStockValidationStrategy {

    private final List<StockValidationStrategy> strategies;

    public CompositeStockValidationStrategy(List<StockValidationStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Kayıtlı tum stratejileri siraya gore calistirir.
     *
     * @return Dogrulama sonucu: tum stratejiler gecti mi?
     */
    public ValidationResult validate(Item item, StockMovementRequest request) {
        boolean passed   = true;
        boolean warning  = false;
        StringBuilder errorMsg = new StringBuilder();

        for (StockValidationStrategy strategy : strategies) {
            boolean ok = strategy.validateMovement(item, request);

            if (!ok) {
                if (strategy instanceof QuantityCheckStrategy) {
                    // Kritik: stok yetersiz → hareket engellenecek
                    passed = false;
                    errorMsg.append(strategy.getErrorMessage(item, request));
                } else if (strategy instanceof MinStockCheckStrategy) {
                    // Uyari: min stock altına dustü → bildirim gonderilecek
                    warning = true;
                }
            }
        }

        return new ValidationResult(passed, warning, errorMsg.toString());
    }

    // -- Inner result class --------------------------------------------------

    /**
     * Dogrulama sonucunu tasir.
     *
     * @param passed  true → hareket gecerli, false → 409 Conflict
     * @param warning true → min stock altina dusuyor, notification gonder
     * @param errorMessage Basarisizlik mesaji (yalnizca !passed durumunda dolu)
     */
    public record ValidationResult(boolean passed, boolean warning, String errorMessage) {}
}
