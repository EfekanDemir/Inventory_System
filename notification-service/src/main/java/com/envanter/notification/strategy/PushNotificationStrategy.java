package com.envanter.notification.strategy;

import com.envanter.notification.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Push bildirim stratejisi — log tabanlı implementasyon.
 *
 * <p>Gerçek mobil push altyapısı (FCM/APNs) gerekmez.
 * Üretimde bu sınıf Firebase/FCM entegrasyonu ile genişletilir.
 * Single Responsibility: Yalnızca push kanalından sorumludur.</p>
 */
@Component
public class PushNotificationStrategy implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationStrategy.class);

    @Override
    public void send(NotificationRequest request) {
        log.info("[PUSH] Bildirim gönderiliyor → alıcı: {}, konu: {}",
                request.getRecipientEmail(), request.getSubject());

        // Simüle: Gerçek FCM/APNs çağrısı yerine log
        log.info("[PUSH] İçerik: {}", request.getContent());

        if (request.getRelatedItemId() != null) {
            log.info("[PUSH] İlgili item ID: {}", request.getRelatedItemId());
        }

        log.info("[PUSH] Başarıyla iletildi (simüle) → {}", request.getRecipientEmail());
        // TODO: Firebase Admin SDK ile gerçek FCM çağrısı
    }

    @Override
    public String getSupportedType() {
        return "PUSH";
    }
}
