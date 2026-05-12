package com.envanter.notification.dto;

/**
 * Bildirim gonderme istek body'si.
 * type alani Factory pattern icin kullanilir: EMAIL, PUSH, LOW_STOCK
 */
public class NotificationRequest {

    private String type; // EMAIL | PUSH | LOW_STOCK
    private String recipientEmail;
    private String subject;
    private String content;
    private Long relatedItemId; // LOW_STOCK uyarilari icin

    public NotificationRequest() {}

    public NotificationRequest(String type, String recipientEmail,
                               String subject, String content) {
        this.type = type;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getRelatedItemId() { return relatedItemId; }
    public void setRelatedItemId(Long relatedItemId) { this.relatedItemId = relatedItemId; }
}
