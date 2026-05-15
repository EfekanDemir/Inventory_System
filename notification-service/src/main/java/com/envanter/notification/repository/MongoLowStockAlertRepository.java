package com.envanter.notification.repository;

import com.envanter.notification.model.LowStockAlert;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * LowStockAlert için MongoTemplate tabanlı repository implementasyonu.
 *
 * <p>Koleksiyon: "low_stock_alerts"</p>
 */
@Repository
public class MongoLowStockAlertRepository implements LowStockAlertRepository {

    private final MongoTemplate mongoTemplate;

    public MongoLowStockAlertRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // -- LowStockAlertRepository impl -----------------------------------------

    @Override
    public LowStockAlert save(@NonNull LowStockAlert alert) {
        return mongoTemplate.save(alert);
    }

    @Override
    public List<LowStockAlert> findAll() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "created_at"));
        return mongoTemplate.find(query, LowStockAlert.class);
    }

    @Override
    public List<LowStockAlert> findByItemId(@NonNull String itemId) {
        Query query = Query.query(Criteria.where("item_id").is(itemId))
                .with(Sort.by(Sort.Direction.DESC, "created_at"));
        return mongoTemplate.find(query, LowStockAlert.class);
    }

    @Override
    public List<LowStockAlert> findByAlertStatus(@NonNull String status) {
        Query query = Query.query(Criteria.where("alert_status").is(status))
                .with(Sort.by(Sort.Direction.DESC, "created_at"));
        return mongoTemplate.find(query, LowStockAlert.class);
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * ID'ye göre tek uyarı getirir.
     */
    public Optional<LowStockAlert> findById(@NonNull String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, LowStockAlert.class));
    }

    /**
     * Belirli bir item'in aktif uyarılarını getirir.
     */
    public List<LowStockAlert> findActiveByItemId(@NonNull String itemId) {
        Query query = Query.query(
                Criteria.where("item_id").is(itemId)
                        .and("alert_status").is("ACTIVE")
        );
        return mongoTemplate.find(query, LowStockAlert.class);
    }

    /**
     * Tüm ACTIVE uyarıları getirir (dashboard için).
     */
    public List<LowStockAlert> findAllActive() {
        return findByAlertStatus("ACTIVE");
    }

    /**
     * ID'ye göre uyarıyı siler.
     */
    public void deleteById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, LowStockAlert.class);
    }

    /**
     * ID'ye göre kayıt var mı?
     */
    public boolean existsById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, LowStockAlert.class);
    }
}
