package com.envanter.notification.service;

import com.envanter.notification.dto.NotificationRequest;
import com.envanter.notification.model.NotificationLog;

import java.util.List;

/**
 * NotificationService arayuzu.
 */
public interface NotificationService {

    /**
     * Bildirimi gonderir ve log'lar.
     *
     * @param request Bildirim istegi (type: EMAIL | PUSH | LOW_STOCK)
     * @throws com.envanter.notification.exception.NotificationValidationException bilinmeyen tip icin
     */
    void sendNotification(NotificationRequest request);

    /**
     * Tum bildirim loglari doner.
     */
    List<NotificationLog> getLogs();

    /**
     * Dusuk stok uyarisi olusturur ve log'lar.
     *
     * @param itemId        Uyari gonderilecek urun ID'si
     * @param currentStock  Mevcut stok miktari
     * @param minStockLevel Minimum stok esigi
     */
    void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel);
}
