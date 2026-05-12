package com.envanter.inventory.factory;

import com.envanter.inventory.strategy.CompositeStockValidationStrategy;
import com.envanter.inventory.strategy.MinStockCheckStrategy;
import com.envanter.inventory.strategy.QuantityCheckStrategy;
import com.envanter.inventory.strategy.StockValidationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StockValidationStrategy nesnelerini uretme ve birlestirme fabrikasi.
 *
 * <p>Open/Closed Principle: Yeni bir strateji eklemek icin sadece
 * bu factory'e kaydetmek yeterlidir. Servis ve composite degismez.</p>
 *
 * Strateji uygulama sirasi onemlidir:
 * <ol>
 *   <li>QuantityCheckStrategy — oncelikli: stok var mi?</li>
 *   <li>MinStockCheckStrategy — ikincil: min esik uyarisi</li>
 * </ol>
 */
@Component
public class StockValidationStrategyFactory {

    private final QuantityCheckStrategy quantityCheckStrategy;
    private final MinStockCheckStrategy minStockCheckStrategy;

    public StockValidationStrategyFactory(
            QuantityCheckStrategy quantityCheckStrategy,
            MinStockCheckStrategy minStockCheckStrategy) {
        this.quantityCheckStrategy = quantityCheckStrategy;
        this.minStockCheckStrategy = minStockCheckStrategy;
    }

    /**
     * Standart stok dogrulama composite'ini doner.
     * (Quantity check → Min stock check)
     */
    public CompositeStockValidationStrategy createDefault() {
        List<StockValidationStrategy> strategies = List.of(
                quantityCheckStrategy,
                minStockCheckStrategy
        );
        return new CompositeStockValidationStrategy(strategies);
    }

    /**
     * Yalnizca miktar kontrolu yapan basit composite doner.
     * (Min stock uyarisi olmadan)
     */
    public CompositeStockValidationStrategy createQuantityOnly() {
        return new CompositeStockValidationStrategy(List.of(quantityCheckStrategy));
    }

    /**
     * Tek bir stratejiyi sarmalayip composite olarak doner.
     * Test ve ozel kullanim icin.
     */
    public CompositeStockValidationStrategy createWith(List<StockValidationStrategy> strategies) {
        return new CompositeStockValidationStrategy(strategies);
    }
}
