package com.envanter.common.generic;

import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * Common Generic CRUD repository contract for all microservices.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface GenericRepository<T, ID> {

    Optional<T> findById(@NonNull ID id);

    List<T> findAll();

    T save(@NonNull T entity);

    void deleteById(@NonNull ID id);

    boolean existsById(@NonNull ID id);
}
