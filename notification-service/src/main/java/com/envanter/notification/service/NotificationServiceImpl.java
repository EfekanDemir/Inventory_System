package com.envanter.notification.service;

import com.envanter.notification.dto.NotificationRequest;
import com.envanter.notification.model.NotificationLog;
import com.envanter.notification.repository.LowStockAlertRepository;
import com.envanter.notification.repository.NotificationLogRepository;

import java.util.List;

/**
 * NotificationService implementasyonu.
 *
 * TDD RED ASAMA -- tum metodlar UnsupportedOperationException firlatir.
 * Implementasyon bir sonraki commit'te (GREEN) eklenecektir.
 *
 * SOLID: Constructor Injection -- Factory + Strategy pattern ile bildirim gonderimi.
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final LowStockAlertRepository lowStockAlertRepository;

    public NotificationServiceImpl(NotificationLogRepository notificationLogRepository,
                                   LowStockAlertRepository lowStockAlertRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.lowStockAlertRepository = lowStockAlertRepository;
    }

    @Override
    public void sendNotification(NotificationRequest request) {
        // TODO: Factory -> Strategy -> send() + log kaydet -- GREEN asamada
        throw new UnsupportedOperationException("sendNotification() RED asama");
    }

    @Override
    public List<NotificationLog> getLogs() {
        // TODO: notificationLogRepository.findAll() -- GREEN asamada
        throw new UnsupportedOperationException("getLogs() RED asama");
    }

    @Override
    public void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel) {
        // TODO: LowStockAlert olustur + kaydet -- GREEN asamada
        throw new UnsupportedOperationException("sendLowStockAlert() RED asama");
    }
}
