package com.envanter.notification.repository;

import com.envanter.notification.model.ActivityLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * ActivityLog için MongoTemplate tabanlı repository.
 *
 * <p>Koleksiyon: "activity_logs"</p>
 * Tüm mikroservislerden gelen AOP/interceptor tabanlı aktivite kayıtlarını depolar.
 */
@Repository
public class MongoActivityLogRepository {

    private final MongoTemplate mongoTemplate;

    public MongoActivityLogRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // -- CRUD -----------------------------------------------------------------

    public ActivityLog save(@NonNull ActivityLog activityLog) {
        if (activityLog.getTimestamp() == null) {
            activityLog.setTimestamp(LocalDateTime.now());
        }
        return mongoTemplate.save(activityLog);
    }

    public Optional<ActivityLog> findById(@NonNull String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, ActivityLog.class));
    }

    public List<ActivityLog> findAll() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    public void deleteById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ActivityLog.class);
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * Belirli bir servise ait logları getirir.
     * Örnek: "inventory-service"
     */
    public List<ActivityLog> findByServiceName(@NonNull String serviceName) {
        Query query = Query.query(Criteria.where("service_name").is(serviceName))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    /**
     * Belirli bir kullanıcının aktivitelerini getirir.
     */
    public List<ActivityLog> findByUserId(@NonNull String userId) {
        Query query = Query.query(Criteria.where("user_id").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    /**
     * İşlem tipine göre filtreler.
     * Örnek: "CREATE_ITEM", "STOCK_MOVEMENT"
     */
    public List<ActivityLog> findByAction(@NonNull String action) {
        Query query = Query.query(Criteria.where("action").is(action))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    /**
     * Servis + işlem tipi kombine filtresi.
     */
    public List<ActivityLog> findByServiceNameAndAction(String serviceName, String action) {
        Query query = Query.query(
                Criteria.where("service_name").is(serviceName)
                        .and("action").is(action)
        ).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    /**
     * Belirli tarih aralığındaki aktiviteleri getirir.
     */
    public List<ActivityLog> findByTimestampBetween(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        Query query = Query.query(
                Criteria.where("timestamp").gte(from).lte(to)
        ).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, ActivityLog.class);
    }

    /**
     * Yavaş işlemleri tespit etmek için eşik değeri üzerindeki logları getirir.
     */
    public List<ActivityLog> findSlowOperations(long thresholdMs) {
        Query query = Query.query(
                Criteria.where("execution_time_ms").gt(thresholdMs)
        ).with(Sort.by(Sort.Direction.DESC, "execution_time_ms"));
        return mongoTemplate.find(query, ActivityLog.class);
    }
}
