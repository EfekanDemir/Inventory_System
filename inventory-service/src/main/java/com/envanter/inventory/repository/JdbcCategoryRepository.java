package com.envanter.inventory.repository;

import com.envanter.inventory.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL CATEGORIES tablosu için JDBC tabanlı repository.
 *
 * <p>DIP: CategoryRepository arayüzünü implement eder.
 * Constructor Injection zorunlu.</p>
 */
@Repository
public class JdbcCategoryRepository implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, description, created_at FROM categories ORDER BY id",
                new CategoryRowMapper());
    }

    @Override
    public Optional<Category> findById(@NonNull Long id) {
        try {
            Category cat = jdbcTemplate.queryForObject(
                    "SELECT id, name, description, created_at FROM categories WHERE id = ?",
                    new CategoryRowMapper(), id);
            return Optional.ofNullable(cat);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        try {
            Category cat = jdbcTemplate.queryForObject(
                    "SELECT id, name, description, created_at FROM categories WHERE LOWER(name) = LOWER(?)",
                    new CategoryRowMapper(), name);
            return Optional.ofNullable(cat);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Category save(@NonNull Category category) {
        if (category.getId() == null) {
            // INSERT
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO categories (name, description, created_at) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, category.getName());
                ps.setString(2, category.getDescription());
                ps.setTimestamp(3, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key != null) {
                category.setId(key.longValue());
            }
            category.setCreatedAt(now);
        } else {
            // UPDATE
            jdbcTemplate.update(
                    "UPDATE categories SET name = ?, description = ? WHERE id = ?",
                    category.getName(), category.getDescription(), category.getId());
        }
        return category;
    }

    @Override
    public void deleteById(@NonNull Long id) {
        jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
    }

    @Override
    public boolean existsById(@NonNull Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------

    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Timestamp ts = rs.getTimestamp("created_at");
            return new Category(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    ts != null ? ts.toLocalDateTime() : null
            );
        }
    }
}
