package com.envanter.notification.strategy;

import com.envanter.notification.dto.NotificationRequest;

/**
 * Push bildirim stratejisi.
 *
 * TDD RED ASAMA -- send() UnsupportedOperationException firlatir.
 * Gercek implementasyonda loglama yapilacak (gercek push gerekmez).
 */
public class PushNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(NotificationRequest request) {
        // TODO: push bildirim / loglama -- GREEN asamada
        throw new UnsupportedOperationException("PushNotificationStrategy.send() RED asama");
    }

    @Override
    public String getSupportedType() {
        return "PUSH";
    }
}
