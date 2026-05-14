package com.envanter.inventory.service;

import com.envanter.inventory.dto.CategoryDTO;
import com.envanter.inventory.dto.CategoryRequest;

import java.util.List;

/**
 * Kategori yönetim servis arayüzü.
 * DIP: Controller bu arayüze bağlıdır.
 */
public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO createCategory(CategoryRequest request);

    CategoryDTO updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
