package com.envanter.notification.factory;

import com.envanter.notification.exception.NotificationValidationException;
import com.envanter.notification.strategy.EmailSenderStrategy;
import com.envanter.notification.strategy.NotificationStrategy;
import com.envanter.notification.strategy.PushNotificationStrategy;
import org.springframework.stereotype.Component;

/**
 * Bildirim stratejisi oluşturucu — Factory Pattern + Spring DI entegrasyonu.
 *
 * <p>Open/Closed Principle: Yeni bir bildirim kanalı eklemek için
 * yeni bir NotificationStrategy impl yazılır ve bu factory'e eklenir.
 * Mevcut kod değişmez.</p>
 *
 * Desteklenen tipler: EMAIL, PUSH, LOW_STOCK
 */
@Component
public class NotificationFactory {

    private final EmailSenderStrategy       emailSenderStrategy;
    private final PushNotificationStrategy  pushNotificationStrategy;

    public NotificationFactory(EmailSenderStrategy emailSenderStrategy,
                               PushNotificationStrategy pushNotificationStrategy) {
        this.emailSenderStrategy      = emailSenderStrategy;
        this.pushNotificationStrategy = pushNotificationStrategy;
    }

    /**
     * Verilen tipe göre uygun NotificationStrategy döner.
     *
     * @param type EMAIL | PUSH | LOW_STOCK
     * @return İlgili strateji implementasyonu (singleton bean)
     * @throws NotificationValidationException bilinmeyen tip için
     */
    public NotificationStrategy createStrategy(String type) {
        if (type == null || type.isBlank()) {
            throw new NotificationValidationException("Bildirim tipi boş olamaz.");
        }

        return switch (type.toUpperCase()) {
            case "EMAIL"     -> emailSenderStrategy;
            case "PUSH"      -> pushNotificationStrategy;
            // LOW_STOCK → e-posta üzerinden gönderilir (varsayılan kanal)
            case "LOW_STOCK",
                 "INVENTORY_ADDED",
                 "STOCK_MOVEMENT" -> emailSenderStrategy;
            default -> throw new NotificationValidationException(
                    "Bilinmeyen bildirim tipi: '" + type +
                    "'. Desteklenen tipler: EMAIL, PUSH, LOW_STOCK, INVENTORY_ADDED, STOCK_MOVEMENT");
        };
    }

    /**
     * Birincil (varsayılan) bildirim stratejisini döner.
     * Genellikle EMAIL kullanılır.
     */
    public NotificationStrategy defaultStrategy() {
        return emailSenderStrategy;
    }
}
