package com.envanter.inventory.repository;

import com.envanter.common.generic.GenericRepository;
import com.envanter.inventory.model.Category;

import java.util.Optional;

/**
 * Category persistence icin repository arayuzu.
 * Implementasyonu: JdbcCategoryRepository.
 */
public interface CategoryRepository extends GenericRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
