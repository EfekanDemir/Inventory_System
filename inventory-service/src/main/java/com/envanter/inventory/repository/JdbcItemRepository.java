package com.envanter.inventory.repository;

import com.envanter.inventory.model.Item;
import com.envanter.inventory.model.ItemStatus;
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
 * Item entity'si icin JDBC tabanli repository implementasyonu.
 * PostgreSQL ITEMS tablosuna karsilik gelir.
 *
 * <p>GenericRepository&lt;Item, Long&gt; sozlesmesini implement eder.</p>
 */
@Repository
public class JdbcItemRepository implements GenericRepository<Item, Long> {

    private final JdbcTemplate jdbc;

    public JdbcItemRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // -- GenericRepository impl -----------------------------------------------

    @Override
    public Optional<Item> findById(Long id) {
        String sql = """
                SELECT id, item_code, name, description, category_id,
                       quantity, min_stock_level, location, status,
                       unit_price, created_at, updated_at
                  FROM items
                 WHERE id = ?
                """;
        List<Item> result = jdbc.query(sql, new ItemRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Item> findAll() {
        String sql = """
                SELECT id, item_code, name, description, category_id,
                       quantity, min_stock_level, location, status,
                       unit_price, created_at, updated_at
                  FROM items
                 ORDER BY id
                """;
        return jdbc.query(sql, new ItemRowMapper());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            return insert(item);
        }
        return update(item);
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM items WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM items WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    // -- Ek sorgular ----------------------------------------------------------

    /**
     * Kod'a gore item getirir.
     */
    public Optional<Item> findByItemCode(String itemCode) {
        String sql = """
                SELECT id, item_code, name, description, category_id,
                       quantity, min_stock_level, location, status,
                       unit_price, created_at, updated_at
                  FROM items
                 WHERE item_code = ?
                """;
        List<Item> result = jdbc.query(sql, new ItemRowMapper(), itemCode);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    /**
     * Stok miktari minStockLevel'in altinda olan aktif itemlari getirir.
     */
    public List<Item> findLowStockItems() {
        String sql = """
                SELECT id, item_code, name, description, category_id,
                       quantity, min_stock_level, location, status,
                       unit_price, created_at, updated_at
                  FROM items
                 WHERE quantity <= min_stock_level
                   AND status = 'ACTIVE'
                """;
        return jdbc.query(sql, new ItemRowMapper());
    }

    /**
     * Kategoriye gore itemlari getirir.
     */
    public List<Item> findByCategoryId(Long categoryId) {
        String sql = """
                SELECT id, item_code, name, description, category_id,
                       quantity, min_stock_level, location, status,
                       unit_price, created_at, updated_at
                  FROM items
                 WHERE category_id = ?
                """;
        return jdbc.query(sql, new ItemRowMapper(), categoryId);
    }

    // -- Private helpers ------------------------------------------------------

    private Item insert(Item item) {
        String sql = """
                INSERT INTO items
                  (item_code, name, description, category_id, quantity,
                   min_stock_level, location, status, unit_price, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getItemCode());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setObject(4, item.getCategoryId());
            ps.setInt(5, item.getQuantity());
            ps.setInt(6, item.getMinStockLevel());
            ps.setString(7, item.getLocation());
            ps.setString(8, item.getStatus() != null ? item.getStatus().name() : ItemStatus.ACTIVE.name());
            ps.setBigDecimal(9, item.getUnitPrice());
            ps.setTimestamp(10, Timestamp.valueOf(now));
            ps.setTimestamp(11, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            item.setId(key.longValue());
        }
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        return item;
    }

    private Item update(Item item) {
        String sql = """
                UPDATE items
                   SET item_code = ?, name = ?, description = ?, category_id = ?,
                       quantity = ?, min_stock_level = ?, location = ?,
                       status = ?, unit_price = ?, updated_at = ?
                 WHERE id = ?
                """;

        LocalDateTime now = LocalDateTime.now();
        jdbc.update(sql,
                item.getItemCode(),
                item.getName(),
                item.getDescription(),
                item.getCategoryId(),
                item.getQuantity(),
                item.getMinStockLevel(),
                item.getLocation(),
                item.getStatus() != null ? item.getStatus().name() : ItemStatus.ACTIVE.name(),
                item.getUnitPrice(),
                Timestamp.valueOf(now),
                item.getId()
        );
        item.setUpdatedAt(now);
        return item;
    }

    // -- RowMapper ------------------------------------------------------------

    private static final class ItemRowMapper implements RowMapper<Item> {

        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemCode(rs.getString("item_code"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            item.setCategoryId(rs.getLong("category_id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setMinStockLevel(rs.getInt("min_stock_level"));
            item.setLocation(rs.getString("location"));

            String statusStr = rs.getString("status");
            if (statusStr != null) {
                item.setStatus(ItemStatus.valueOf(statusStr));
            }

            item.setUnitPrice(rs.getBigDecimal("unit_price"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) item.setCreatedAt(createdAt.toLocalDateTime());

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) item.setUpdatedAt(updatedAt.toLocalDateTime());

            return item;
        }
    }
}
