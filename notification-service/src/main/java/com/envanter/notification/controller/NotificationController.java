package com.envanter.notification.controller;

import com.envanter.notification.dto.NotificationRequest;
import com.envanter.notification.model.NotificationLog;
import com.envanter.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bildirim yönetim controller'ı.
 *
 * <p>DIP: NotificationService arayüzüne bağımlı — implementasyon detayını bilmez.
 * Constructor Injection zorunlu — @Autowired field injection yasak.</p>
 *
 * Endpointler:
 * - POST /api/notifications/send          → bildirim gönder
 * - GET  /api/notifications/logs          → tüm bildirim logları
 * - POST /api/notifications/low-stock     → düşük stok uyarısı
 * - GET  /api/notifications/health        → sağlık kontrolü
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    /** DIP: Somut impl değil, arayüz inject edildi. */
    private final NotificationService notificationService;

    /** Constructor Injection — Spring tek constructor varsa @Autowired gerekmez. */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // -------------------------------------------------------------------------
    // Endpoints
    // -------------------------------------------------------------------------

    /**
     * Bildirim gönderir (EMAIL / PUSH / LOW_STOCK).
     * POST /api/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        log.info("Bildirim isteği: tip={}, alıcı={}", request.getType(), request.getRecipientEmail());
        notificationService.sendNotification(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Tüm bildirim loglarını döner.
     * GET /api/notifications/logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<NotificationLog>> getLogs() {
        return ResponseEntity.ok(notificationService.getLogs());
    }

    /**
     * Düşük stok uyarısı — inventory-service tarafından tetiklenir.
     * POST /api/notifications/low-stock
     */
    @PostMapping("/low-stock")
    public ResponseEntity<Void> sendLowStockAlert(
            @RequestParam Long itemId,
            @RequestParam int currentStock,
            @RequestParam int minStockLevel) {
        log.warn("Düşük stok uyarısı: itemId={}, stock={}, min={}", itemId, currentStock, minStockLevel);
        notificationService.sendLowStockAlert(itemId, currentStock, minStockLevel);
        return ResponseEntity.accepted().build();
    }

    /**
     * Servis sağlık kontrolü.
     * GET /api/notifications/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("notification-service UP");
    }
}
