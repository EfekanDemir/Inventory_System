package com.envanter.inventory.client;

import com.envanter.inventory.client.dto.InventoryAddedRequest;
import com.envanter.inventory.client.dto.LowStockAlertRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * NotificationClient'in RestTemplate tabanlı HTTP implementasyonu.
 *
 * <p>Constructor Injection — @Autowired yasak.
 * Tüm çağrılar asenkrondur (@Async) → inventory iş akışını bloklamaz.
 * Hata durumunda try-catch + log ile fallback uygulanır (circuit-breaker olmadan).</p>
 *
 * Çağrılan endpoint'ler:
 * - POST {notificationServiceUrl}/api/notifications/low-stock
 * - POST {notificationServiceUrl}/api/notifications/inventory-added
 */
@Component
public class NotificationClientImpl implements NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClientImpl.class);

    /** notification-service base URL — docker-compose'da http://notification-service:8083 */
    @Value("${notification.service.url:http://notification-service:8083}")
    private String notificationServiceUrl;

    private final RestTemplate restTemplate;

    public NotificationClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // -------------------------------------------------------------------------
    // NotificationClient impl
    // -------------------------------------------------------------------------

    /**
     * Düşük stok uyarısı gönderir.
     * @Async: inventory iş akışını bloklamaz; ThreadPoolTaskExecutor'da çalışır.
     */
    @Async
    @Override
    public void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel) {
        String url = notificationServiceUrl + "/api/notifications/low-stock"
                + "?itemId=" + itemId
                + "&currentStock=" + currentStock
                + "&minStockLevel=" + minStockLevel;

        log.warn("[NotificationClient] Düşük stok uyarısı gönderiliyor → itemId={}, stock={}, min={}",
                itemId, currentStock, minStockLevel);

        try {
            restTemplate.postForEntity(url, buildJsonEntity(null), Void.class);
            log.info("[NotificationClient] Düşük stok uyarısı iletildi → itemId={}", itemId);
        } catch (RestClientException ex) {
            // Fallback: hata loglanır, inventory akışı kesilmez
            log.error("[NotificationClient] Düşük stok uyarısı gönderilemedi → itemId={}, hata={}",
                    itemId, ex.getMessage());
        }
    }

    /**
     * Yeni envanter eklendiğinde bildirim gönderir.
     * @Async: inventory iş akışını bloklamaz.
     */
    @Async
    @Override
    public void sendInventoryAddedNotification(Long itemId, String itemName) {
        String url = notificationServiceUrl + "/api/notifications/inventory-added";

        InventoryAddedRequest body = new InventoryAddedRequest(itemId, itemName);

        log.info("[NotificationClient] Envanter eklendi bildirimi gönderiliyor → itemId={}, name='{}'",
                itemId, itemName);

        try {
            restTemplate.postForEntity(url, buildJsonEntity(body), Void.class);
            log.info("[NotificationClient] Envanter eklendi bildirimi iletildi → itemId={}", itemId);
        } catch (RestClientException ex) {
            // Fallback: hata loglanır, inventory akışı kesilmez
            log.error("[NotificationClient] Envanter bildirimi gönderilemedi → itemId={}, hata={}",
                    itemId, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * application/json Content-Type başlıklı HttpEntity oluşturur.
     */
    private <T> HttpEntity<T> buildJsonEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Source-Service", "inventory-service");
        return new HttpEntity<>(body, headers);
    }
}
