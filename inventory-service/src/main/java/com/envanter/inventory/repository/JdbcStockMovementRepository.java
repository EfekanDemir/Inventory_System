package com.envanter.inventory.repository;

import com.envanter.inventory.model.MovementType;
import com.envanter.inventory.model.StockMovement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * StockMovement entity'si icin JDBC tabanli repository implementasyonu.
 * PostgreSQL STOCK_MOVEMENTS tablosuna karsilik gelir.
 *
 * <p>GenericRepository&lt;StockMovement, Long&gt; sozlesmesini implement eder.</p>
 */
@Repository
public class JdbcStockMovementRepository implements GenericRepository<StockMovement, Long> {

    private final JdbcTemplate jdbc;

    public JdbcStockMovementRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // -- GenericRepository impl -----------------------------------------------

    @Override
    public Optional<StockMovement> findById(Long id) {
        String sql = """
                SELECT id, item_id, movement_type, quantity,
                       reason, user_id, movement_date, reference_number
                  FROM stock_movements
                 WHERE id = ?
                """;
        List<StockMovement> result = jdbc.query(sql, new StockMovementRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<StockMovement> findAll() {
        String sql = """
                SELECT id, item_id, movement_type, quantity,
                       reason, user_id, movement_date, reference_number
                  FROM stock_movements
                 ORDER BY movement_date DESC
                """;
        return jdbc.query(sql, new StockMovementRowMapper());
    }

    @Override
    public StockMovement save(StockMovement movement) {
        if (movement.getId() == null) {
            return insert(movement);
        }
        return update(movement);
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM stock_movements WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM stock_movements WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * Belirli bir item'in tum stok hareketlerini getirir.
     */
    public List<StockMovement> findByItemId(Long itemId) {
        String sql = """
                SELECT id, item_id, movement_type, quantity,
                       reason, user_id, movement_date, reference_number
                  FROM stock_movements
                 WHERE item_id = ?
                 ORDER BY movement_date DESC
                """;
        return jdbc.query(sql, new StockMovementRowMapper(), itemId);
    }

    /**
     * Hareket tipine gore filtreler (IN veya OUT).
     */
    public List<StockMovement> findByMovementType(MovementType type) {
        String sql = """
                SELECT id, item_id, movement_type, quantity,
                       reason, user_id, movement_date, reference_number
                  FROM stock_movements
                 WHERE movement_type = ?
                 ORDER BY movement_date DESC
                """;
        return jdbc.query(sql, new StockMovementRowMapper(), type.name());
    }

    /**
     * Tarih araligina gore stok hareketlerini getirir.
     */
    public List<StockMovement> findByDateRange(LocalDateTime from, LocalDateTime to) {
        String sql = """
                SELECT id, item_id, movement_type, quantity,
                       reason, user_id, movement_date, reference_number
                  FROM stock_movements
                 WHERE movement_date BETWEEN ? AND ?
                 ORDER BY movement_date DESC
                """;
        return jdbc.query(sql, new StockMovementRowMapper(),
                Timestamp.valueOf(from), Timestamp.valueOf(to));
    }

    // -- Private helpers ------------------------------------------------------

    private StockMovement insert(StockMovement movement) {
        String sql = """
                INSERT INTO stock_movements
                  (item_id, movement_type, quantity, reason, user_id, movement_date, reference_number)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = movement.getMovementDate() != null
                ? movement.getMovementDate()
                : LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, movement.getItemId());
            ps.setString(2, movement.getMovementType() != null
                    ? movement.getMovementType().name() : null);
            ps.setInt(3, movement.getQuantity());
            ps.setString(4, movement.getReason());
            ps.setObject(5, movement.getUserId());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setString(7, movement.getReferenceNumber());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            movement.setId(key.longValue());
        }
        movement.setMovementDate(now);
        return movement;
    }

    private StockMovement update(StockMovement movement) {
        String sql = """
                UPDATE stock_movements
                   SET item_id = ?, movement_type = ?, quantity = ?,
                       reason = ?, user_id = ?, movement_date = ?, reference_number = ?
                 WHERE id = ?
                """;
        jdbc.update(sql,
                movement.getItemId(),
                movement.getMovementType() != null ? movement.getMovementType().name() : null,
                movement.getQuantity(),
                movement.getReason(),
                movement.getUserId(),
                movement.getMovementDate() != null
                        ? Timestamp.valueOf(movement.getMovementDate()) : null,
                movement.getReferenceNumber(),
                movement.getId()
        );
        return movement;
    }

    // -- RowMapper ------------------------------------------------------------

    private static final class StockMovementRowMapper implements RowMapper<StockMovement> {

        @Override
        public StockMovement mapRow(ResultSet rs, int rowNum) throws SQLException {
            StockMovement sm = new StockMovement();
            sm.setId(rs.getLong("id"));
            sm.setItemId(rs.getLong("item_id"));

            String type = rs.getString("movement_type");
            if (type != null) {
                sm.setMovementType(MovementType.valueOf(type));
            }

            sm.setQuantity(rs.getInt("quantity"));
            sm.setReason(rs.getString("reason"));
            sm.setUserId(rs.getLong("user_id"));
            sm.setReferenceNumber(rs.getString("reference_number"));

            Timestamp movementDate = rs.getTimestamp("movement_date");
            if (movementDate != null) sm.setMovementDate(movementDate.toLocalDateTime());

            return sm;
        }
    }
}
