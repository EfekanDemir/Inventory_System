package com.envanter.inventory.repository;

import com.envanter.inventory.model.Item;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Item için JDBC tabanlı repository.
 *
 * <p>DIP: ItemRepository arayüzünü implement eder — somut bağımlılık yok.
 * Constructor Injection zorunlu — @Autowired field injection yasak.</p>
 */
@Repository
public class JdbcItemRepository implements ItemRepository {

    /** Constructor Injection. */
    private final JdbcTemplate jdbcTemplate;

    public JdbcItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // ItemRepository impl
    // -------------------------------------------------------------------------

    @Override
    public Optional<Item> findById(Long id) {
        try {
            Item item = jdbcTemplate.queryForObject(
                    "SELECT * FROM items WHERE id = ?",
                    new ItemRowMapper(), id);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Item> findByItemCode(String itemCode) {
        try {
            Item item = jdbcTemplate.queryForObject(
                    "SELECT * FROM items WHERE item_code = ?",
                    new ItemRowMapper(), itemCode);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll() {
        return jdbcTemplate.query("SELECT * FROM items ORDER BY id", new ItemRowMapper());
    }

    @Override
    public List<Item> findByCategoryId(Long categoryId) {
        return jdbcTemplate.query(
                "SELECT * FROM items WHERE category_id = ? ORDER BY id",
                new ItemRowMapper(), categoryId);
    }

    @Override
    public List<Item> findLowStockItems() {
        return jdbcTemplate.query(
                "SELECT * FROM items WHERE quantity <= min_stock_level",
                new ItemRowMapper());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            // INSERT
            jdbcTemplate.update(
                    "INSERT INTO items (item_code, name, description, category_id, quantity, " +
                    "min_stock_level, location, status, unit_price, created_at, updated_at) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    item.getItemCode(), item.getName(), item.getDescription(),
                    item.getCategoryId(), item.getQuantity(), item.getMinStockLevel(),
                    item.getLocation(), item.getStatus().name(),
                    item.getUnitPrice(), item.getCreatedAt(), item.getUpdatedAt());
            Long id = jdbcTemplate.queryForObject(
                    "SELECT currval(pg_get_serial_sequence('items','id'))", Long.class);
            item.setId(id);
        } else {
            // UPDATE
            jdbcTemplate.update(
                    "UPDATE items SET item_code=?, name=?, description=?, category_id=?, quantity=?, " +
                    "min_stock_level=?, location=?, status=?, unit_price=?, updated_at=? WHERE id=?",
                    item.getItemCode(), item.getName(), item.getDescription(),
                    item.getCategoryId(), item.getQuantity(), item.getMinStockLevel(),
                    item.getLocation(), item.getStatus().name(),
                    item.getUnitPrice(), item.getUpdatedAt(), item.getId());
        }
        return item;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM items WHERE id = ?", id);
    }

    @Override
    public boolean existsByItemCode(String itemCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM items WHERE item_code = ?", Integer.class, itemCode);
        return count != null && count > 0;
    }

    /** ItemRepository arayüzünde tanımlı extra metot. */
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM items WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------

    private static class ItemRowMapper implements RowMapper<Item> {
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
            item.setStatus(com.envanter.inventory.model.ItemStatus.valueOf(rs.getString("status")));
            BigDecimal price = rs.getBigDecimal("unit_price");
            item.setUnitPrice(price);
            item.setCreatedAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            item.setUpdatedAt(rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return item;
        }
    }
}
