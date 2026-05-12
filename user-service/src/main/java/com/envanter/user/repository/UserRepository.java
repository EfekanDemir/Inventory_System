package com.envanter.user.repository;

import com.envanter.user.model.User;

import java.util.Optional;

/**
 * Repository interface for User persistence.
 * Implemented by JdbcUserRepository.
 */
public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
