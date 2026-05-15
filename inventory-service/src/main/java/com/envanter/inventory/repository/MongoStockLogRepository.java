package com.envanter.inventory.repository;

import com.envanter.inventory.model.StockLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.envanter.common.generic.GenericRepository;
import org.springframework.lang.NonNull;
import java.time.LocalDateTime;

/**
 * MongoDB tabanli stok hareket log repository'si.
 *
 * <p>
 * GenericRepository&lt;StockLog, String&gt; sozlesmesini implement eder
 * (MongoDB _id String tipindedir).
 * </p>
 *
 * Koleksiyon: "stock_logs"
 * Amac: denetim izi, gecmis hareket analizi ve hizli raporlama.
 */
@Repository
public class MongoStockLogRepository implements GenericRepository<StockLog, String> {

    private final MongoTemplate mongoTemplate;

    public MongoStockLogRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // -- GenericRepository impl -----------------------------------------------

    @Override
    public Optional<StockLog> findById(@NonNull String id) {
        StockLog result = mongoTemplate.findById(id, StockLog.class);
        return Optional.ofNullable(result);
    }

    @Override
    public List<StockLog> findAll() {
        return mongoTemplate.findAll(StockLog.class);
    }

    @Override
    public StockLog save(@NonNull StockLog stockLog) {
        return mongoTemplate.save(stockLog);
    }

    @Override
    public void deleteById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, StockLog.class);
    }

    @Override
    public boolean existsById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, StockLog.class);
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * Belirli bir item'in tum log kayitlarini getirir.
     */
    public List<StockLog> findByItemId(Long itemId) {
        Query query = Query.query(Criteria.where("item_id").is(itemId));
        return mongoTemplate.find(query, StockLog.class);
    }

    /**
     * Belirli bir PostgreSQL movement ID'sine gore log getirir.
     */
    public Optional<StockLog> findByMovementId(Long movementId) {
        Query query = Query.query(Criteria.where("movement_id").is(movementId));
        StockLog result = mongoTemplate.findOne(query, StockLog.class);
        return Optional.ofNullable(result);
    }

    /**
     * Hareket tipine gore filtreler (IN / OUT).
     */
    public List<StockLog> findByMovementType(String movementType) {
        Query query = Query.query(Criteria.where("movement_type").is(movementType));
        return mongoTemplate.find(query, StockLog.class);
    }

    /**
     * Tarih araligina gore log kayitlarini getirir.
     */
    public List<StockLog> findByMovementDateBetween(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        Query query = Query.query(
                Criteria.where("movement_date").gte(from).lte(to));
        return mongoTemplate.find(query, StockLog.class);
    }

    /**
     * Item kodu ile hizli arama.
     */
    public List<StockLog> findByItemCode(String itemCode) {
        Query query = Query.query(Criteria.where("item_code").is(itemCode));
        return mongoTemplate.find(query, StockLog.class);
    }

    /**
     * En son N log kaydini getirir.
     */
    public List<StockLog> findLatest(int limit) {
        Query query = new Query()
                .limit(limit);
        query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "movement_date"));
        return mongoTemplate.find(query, StockLog.class);
    }

    /** Tüm logları siler. */
    public void clearAll() {
        mongoTemplate.dropCollection(StockLog.class);
    }
}
