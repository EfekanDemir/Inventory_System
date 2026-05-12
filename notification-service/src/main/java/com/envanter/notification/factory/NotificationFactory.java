package com.envanter.notification.factory;

import com.envanter.notification.exception.NotificationValidationException;
import com.envanter.notification.strategy.EmailSenderStrategy;
import com.envanter.notification.strategy.NotificationStrategy;
import com.envanter.notification.strategy.PushNotificationStrategy;

/**
 * Bildirim stratejisi olusturucu -- Factory Pattern.
 *
 * Kullanim:
 *   NotificationStrategy strategy = NotificationFactory.createStrategy("EMAIL");
 *   strategy.send(request);
 *
 * Desteklenen tipler: EMAIL, PUSH
 * Bilinmeyen tip -> NotificationValidationException (HTTP 400)
 */
public class NotificationFactory {

    private NotificationFactory() {
        // Utility sinif -- instantiate edilemez
    }

    /**
     * Verilen tipe gore uygun NotificationStrategy doner.
     *
     * @param type EMAIL | PUSH
     * @return Ilgili strateji implementasyonu
     * @throws NotificationValidationException bilinmeyen tip icin
     */
    public static NotificationStrategy createStrategy(String type) {
        if (type == null || type.isBlank()) {
            throw new NotificationValidationException("Bildirim tipi bos olamaz.");
        }

        switch (type.toUpperCase()) {
            case "EMAIL":
                return new EmailSenderStrategy();
            case "PUSH":
                return new PushNotificationStrategy();
            default:
                throw new NotificationValidationException(
                        "Bilinmeyen bildirim tipi: '" + type + "'. Desteklenen tipler: EMAIL, PUSH"
                );
        }
    }
}
