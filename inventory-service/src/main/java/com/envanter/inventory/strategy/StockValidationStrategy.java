package com.envanter.inventory.strategy;

import com.envanter.inventory.dto.StockMovementRequest;
import com.envanter.inventory.model.Item;

/**
 * Stok hareketi dogrulama stratejisi sozlesmesi.
 *
 * <p>Open/Closed Principle: Yeni bir dogrulama kurali eklemek icin
 * bu arayuzu implement eden yeni bir sinif yazmak yeterlidir.
 * Mevcut kodda degisiklik gerekmez.</p>
 *
 * Uygulama sirasi: CompositeStockValidationStrategy icinde yonetilir.
 */
public interface StockValidationStrategy {

    /**
     * Stok hareketinin gecerli olup olmadigini kontrol eder.
     *
     * @param item    Harekete konu olan envanter kalemi
     * @param request Harekete ait istek bilgileri
     * @return true → hareket gecerli; false → hareket reddedilmeli
     */
    boolean validateMovement(Item item, StockMovementRequest request);

    /**
     * Dogrulama basarisiz oldugunda kullaniciya gosterilecek hata mesaji.
     *
     * @param item    Harekete konu olan envanter kalemi
     * @param request Harekete ait istek bilgileri
     * @return Okunabilir hata mesaji
     */
    String getErrorMessage(Item item, StockMovementRequest request);
}
