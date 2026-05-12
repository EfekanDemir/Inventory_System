package com.envanter.notification.repository;

import com.envanter.notification.model.NotificationLog;

import java.util.List;

/**
 * NotificationLog persistence icin repository arayuzu.
 * MongoDB uzerinde calisir (MongoTemplate ile implemente edilecek).
 */
public interface NotificationLogRepository {

    NotificationLog save(NotificationLog log);

    List<NotificationLog> findAll();

    List<NotificationLog> findByType(String notificationType);

    List<NotificationLog> findByStatus(String status);

    List<NotificationLog> findByRecipientEmail(String email);
}
