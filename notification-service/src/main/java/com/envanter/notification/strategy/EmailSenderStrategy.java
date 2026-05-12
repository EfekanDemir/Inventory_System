package com.envanter.notification.strategy;

import com.envanter.notification.dto.NotificationRequest;

/**
 * Email gonderme stratejisi.
 *
 * TDD RED ASAMA -- send() UnsupportedOperationException firlatir.
 * Gercek implementasyonda JavaMailSender kullanilacak (mock SMTP).
 */
public class EmailSenderStrategy implements NotificationStrategy {

    @Override
    public void send(NotificationRequest request) {
        // TODO: JavaMailSender ile gercek implementasyon -- GREEN asamada
        throw new UnsupportedOperationException("EmailSenderStrategy.send() RED asama");
    }

    @Override
    public String getSupportedType() {
        return "EMAIL";
    }
}
