package com.envanter.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Bildirim log kaydi — MongoDB "notification_logs" koleksiyonuna persist edilir.
 *
 * <p>NoSQL / Şemasız yapı: metadata alanı esnek Map&lt;String,Object&gt; olarak
 * tutulur — her bildirim tipine özel veriler buraya eklenir.</p>
 *
 * İndeksler:
 * - notificationType → sık tip bazlı sorgular
 * - status           → başarısız/bekleyen bildirimleri bulma
 * - sentAt           → tarih bazlı raporlama
 */
@Document(collection = "notification_logs")
@CompoundIndexes({
        @CompoundIndex(name = "idx_type_status", def = "{'notification_type': 1, 'status': 1}"),
        @CompoundIndex(name = "idx_recipient_sent", def = "{'recipient_email': 1, 'sent_at': -1}")
})
public class NotificationLog {

    @Id
    private String id;

    @Field("recipient_email")
    @Indexed
    private String recipientEmail;

    /**
     * LOW_STOCK | INVENTORY_ADDED | STOCK_MOVEMENT
     */
    @Field("notification_type")
    @Indexed
    private String notificationType;

    @Field("subject")
    private String subject;

    @Field("content")
    private String content;

    /**
     * SENT | FAILED | PENDING
     */
    @Field("status")
    @Indexed
    private String status;

    @Field("sent_at")
    @Indexed
    private LocalDateTime sentAt;

    /** Esnek metadata: itemId, itemCode, stockLevel vb. */
    @Field("metadata")
    private Map<String, Object> metadata;

    // -- Constructors ---------------------------------------------------------

    public NotificationLog() {}

    public NotificationLog(String recipientEmail,
                           String notificationType,
                           String subject,
                           String content,
                           String status) {
        this.recipientEmail    = recipientEmail;
        this.notificationType  = notificationType;
        this.subject           = subject;
        this.content           = content;
        this.status            = status;
        this.sentAt            = LocalDateTime.now();
    }

    // -- Getters & Setters ----------------------------------------------------

    public String getId()                           { return id; }
    public void setId(String id)                    { this.id = id; }

    public String getRecipientEmail()               { return recipientEmail; }
    public void setRecipientEmail(String email)     { this.recipientEmail = email; }

    public String getNotificationType()             { return notificationType; }
    public void setNotificationType(String type)    { this.notificationType = type; }

    public String getSubject()                      { return subject; }
    public void setSubject(String subject)          { this.subject = subject; }

    public String getContent()                      { return content; }
    public void setContent(String content)          { this.content = content; }

    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }

    public LocalDateTime getSentAt()                { return sentAt; }
    public void setSentAt(LocalDateTime sentAt)     { this.sentAt = sentAt; }

    public Map<String, Object> getMetadata()                    { return metadata; }
    public void setMetadata(Map<String, Object> metadata)       { this.metadata = metadata; }
}
