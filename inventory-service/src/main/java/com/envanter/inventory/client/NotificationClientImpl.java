package com.envanter.inventory.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * NotificationClient'in HTTP implementasyonu.
 *
 * <p>Simdilik log tabanlidir. İleride RestTemplate / WebClient ile
 * notification-service'e gercek HTTP istegi atacak sekilde genisletilir.
 * Test sirasinda bu sinif Mockito ile mock'lanir.</p>
 */
@Component
public class NotificationClientImpl implements NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClientImpl.class);

    @Override
    public void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel) {
        log.warn("[NOTIFICATION] Dusuk stok uyarisi → itemId={}, mevcutStok={}, minEsik={}",
                itemId, currentStock, minStockLevel);
        // TODO: RestTemplate ile notification-service /api/notifications/low-stock POST
    }

    @Override
    public void sendInventoryAddedNotification(Long itemId, String itemName) {
        log.info("[NOTIFICATION] Yeni envanter eklendi → itemId={}, ad='{}'",
                itemId, itemName);
        // TODO: RestTemplate ile notification-service /api/notifications/inventory-added POST
    }
}
