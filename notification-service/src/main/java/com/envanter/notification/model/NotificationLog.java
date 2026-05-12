package com.envanter.notification.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Bildirim log kaydi -- MongoDB'ye @Document olarak persist edilir.
 * notification-service icin NoSQL dokuman modeli.
 */
public class NotificationLog {

    private String id; // MongoDB ObjectId (String olarak temsil edilir)
    private String recipientEmail;
    private String notificationType; // EMAIL, PUSH, LOW_STOCK
    private String subject;
    private String content;
    private String status; // SENT, FAILED, PENDING
    private LocalDateTime sentAt;
    private Map<String, Object> metadata;

    public NotificationLog() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
