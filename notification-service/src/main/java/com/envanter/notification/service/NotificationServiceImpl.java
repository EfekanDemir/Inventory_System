package com.envanter.notification.service;

import com.envanter.notification.dto.NotificationRequest;
import com.envanter.notification.factory.NotificationFactory;
import com.envanter.notification.model.LowStockAlert;
import com.envanter.notification.model.NotificationLog;
import com.envanter.notification.repository.MongoLowStockAlertRepository;
import com.envanter.notification.repository.MongoNotificationLogRepository;
import com.envanter.notification.strategy.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NotificationService implementasyonu — TDD GREEN asama.
 *
 * <p>İş akışı (sendNotification):</p>
 * <ol>
 *   <li>NotificationFactory → uygun Strategy seçilir</li>
 *   <li>Strategy.send(request) çağrılır</li>
 *   <li>Başarılı/başarısız olursa MongoDB'ye NotificationLog kaydedilir</li>
 * </ol>
 *
 * <p>sendLowStockAlert: LOW_STOCK logu + LowStockAlert belgesi kaydeder,
 * ardından EMAIL kanalıyla bildirim gönderir.</p>
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String DEFAULT_ALERT_EMAIL = "warehouse@envanter.com";

    private final NotificationFactory             notificationFactory;
    private final MongoNotificationLogRepository  notificationLogRepo;
    private final MongoLowStockAlertRepository    lowStockAlertRepo;

    public NotificationServiceImpl(
            NotificationFactory notificationFactory,
            MongoNotificationLogRepository notificationLogRepo,
            MongoLowStockAlertRepository lowStockAlertRepo) {
        this.notificationFactory = notificationFactory;
        this.notificationLogRepo = notificationLogRepo;
        this.lowStockAlertRepo   = lowStockAlertRepo;
    }

    // -------------------------------------------------------------------------
    // NotificationService impl
    // -------------------------------------------------------------------------

    @Override
    public void sendNotification(NotificationRequest request) {
        NotificationStrategy strategy = notificationFactory.createStrategy(request.getType());

        String status = "SENT";
        String errorDetail = null;

        try {
            strategy.send(request);
            log.info("[NotificationService] Bildirim gönderildi → tip: {}, alıcı: {}",
                    request.getType(), request.getRecipientEmail());
        } catch (Exception ex) {
            status = "FAILED";
            errorDetail = ex.getMessage();
            log.error("[NotificationService] Bildirim başarısız → tip: {}, hata: {}",
                    request.getType(), ex.getMessage());
        }

        // Her durumda MongoDB'ye log yaz
        saveNotificationLog(request, status, errorDetail);
    }

    @Override
    public List<NotificationLog> getLogs() {
        return notificationLogRepo.findAll();
    }

    @Override
    public void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel) {
        log.warn("[NotificationService] Düşük stok uyarısı → itemId={}, stok={}, min={}",
                itemId, currentStock, minStockLevel);

        // 1) LowStockAlert dökümanı kaydet (ACTIVE)
        LowStockAlert alert = new LowStockAlert(
                String.valueOf(itemId),
                "Item #" + itemId,   // itemName: detaylı servis çağrısı ileride eklenebilir
                currentStock,
                minStockLevel
        );
        lowStockAlertRepo.save(alert);

        // 2) LOW_STOCK tipiyle bildirim gönder (EMAIL kanalı)
        NotificationRequest alertRequest = buildLowStockRequest(itemId, currentStock, minStockLevel);
        sendNotification(alertRequest);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void saveNotificationLog(NotificationRequest request,
                                     String status,
                                     String errorDetail) {
        NotificationLog notifLog = new NotificationLog(
                request.getRecipientEmail(),
                request.getType(),
                request.getSubject(),
                request.getContent(),
                status
        );
        notifLog.setSentAt(LocalDateTime.now());

        // Hata detayı varsa metadata'ya ekle
        if (errorDetail != null) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("error", errorDetail);
            if (request.getRelatedItemId() != null) {
                meta.put("itemId", request.getRelatedItemId());
            }
            notifLog.setMetadata(meta);
        } else if (request.getRelatedItemId() != null) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("itemId", request.getRelatedItemId());
            notifLog.setMetadata(meta);
        }

        notificationLogRepo.save(notifLog);
    }

    private NotificationRequest buildLowStockRequest(Long itemId,
                                                      int currentStock,
                                                      int minStockLevel) {
        NotificationRequest req = new NotificationRequest();
        req.setType("LOW_STOCK");
        req.setRecipientEmail(DEFAULT_ALERT_EMAIL);
        req.setSubject("[UYARI] Düşük Stok: Item #" + itemId);
        req.setContent(String.format(
                "Item #%d için stok düşük!\n" +
                "Mevcut stok : %d\n" +
                "Minimum eşik: %d\n\n" +
                "Lütfen stok yenilemesi yapınız.",
                itemId, currentStock, minStockLevel
        ));
        req.setRelatedItemId(itemId);
        return req;
    }
}
