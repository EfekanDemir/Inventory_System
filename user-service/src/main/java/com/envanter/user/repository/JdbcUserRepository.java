package com.envanter.user.repository;

import com.envanter.user.model.Role;
import com.envanter.user.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements GenericRepository<User, Long> {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    Role.valueOf(rs.getString("role")),
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                    rs.getBoolean("is_active")
            );
        }
    };

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            // Insert
            String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name, role, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW()) RETURNING id, created_at, updated_at";
            
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                entity.setId(rs.getLong("id"));
                entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return entity;
            }, entity.getUsername(), entity.getEmail(), entity.getPasswordHash(), 
               entity.getFirstName(), entity.getLastName(), entity.getRole().name(), entity.isActive());
        } else {
            // Update
            String sql = "UPDATE users SET username=?, email=?, password_hash=?, first_name=?, last_name=?, role=?, is_active=?, updated_at=NOW() " +
                    "WHERE id=? RETURNING updated_at";
            
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return entity;
            }, entity.getUsername(), entity.getEmail(), entity.getPasswordHash(), 
               entity.getFirstName(), entity.getLastName(), entity.getRole().name(), entity.isActive(), entity.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
