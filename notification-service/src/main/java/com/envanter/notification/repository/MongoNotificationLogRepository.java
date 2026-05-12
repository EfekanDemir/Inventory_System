package com.envanter.notification.repository;

import com.envanter.notification.model.NotificationLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * NotificationLog için MongoTemplate tabanlı repository implementasyonu.
 *
 * <p>Koleksiyon: "notification_logs"</p>
 * İndeksler: notificationType, status, sentAt (model üzerinde tanımlı)
 */
@Repository
public class MongoNotificationLogRepository implements NotificationLogRepository {

    private final MongoTemplate mongoTemplate;

    public MongoNotificationLogRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // -- NotificationLogRepository impl ---------------------------------------

    @Override
    public NotificationLog save(NotificationLog log) {
        if (log.getSentAt() == null) {
            log.setSentAt(LocalDateTime.now());
        }
        return mongoTemplate.save(log);
    }

    @Override
    public List<NotificationLog> findAll() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    @Override
    public List<NotificationLog> findByType(String notificationType) {
        Query query = Query.query(Criteria.where("notification_type").is(notificationType))
                .with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    @Override
    public List<NotificationLog> findByStatus(String status) {
        Query query = Query.query(Criteria.where("status").is(status))
                .with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    @Override
    public List<NotificationLog> findByRecipientEmail(String email) {
        Query query = Query.query(Criteria.where("recipient_email").is(email))
                .with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * ID'ye göre tek kayıt getirir.
     */
    public Optional<NotificationLog> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, NotificationLog.class));
    }

    /**
     * Bildirim tipi ve durumuna göre kombine filtre.
     */
    public List<NotificationLog> findByTypeAndStatus(String type, String status) {
        Query query = Query.query(
                Criteria.where("notification_type").is(type)
                        .and("status").is(status)
        ).with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    /**
     * Belirtilen tarih aralığında gönderilen bildirimleri getirir.
     */
    public List<NotificationLog> findBySentAtBetween(LocalDateTime from, LocalDateTime to) {
        Query query = Query.query(
                Criteria.where("sent_at").gte(from).lte(to)
        ).with(Sort.by(Sort.Direction.DESC, "sent_at"));
        return mongoTemplate.find(query, NotificationLog.class);
    }

    /**
     * Belirli bir kaydı siler.
     */
    public void deleteById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, NotificationLog.class);
    }

    /**
     * ID'ye göre kayıt var mı?
     */
    public boolean existsById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, NotificationLog.class);
    }
}
