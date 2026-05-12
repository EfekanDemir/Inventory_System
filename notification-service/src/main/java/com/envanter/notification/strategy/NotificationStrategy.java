package com.envanter.notification.strategy;

import com.envanter.notification.dto.NotificationRequest;

/**
 * Bildirim gonderme stratejisi.
 * Strategy Pattern -- her kanal icin ayri implementasyon.
 *
 * Implementasyonlar:
 *   - EmailSenderStrategy
 *   - PushNotificationStrategy
 */
public interface NotificationStrategy {

    /**
     * Bildirimi gonderir.
     *
     * @param request Bildirim istegi (alici, konu, icerik, tip)
     */
    void send(NotificationRequest request);

    /**
     * Bu stratejinin hangi tipi destekledigini doner (EMAIL, PUSH).
     */
    String getSupportedType();
}
