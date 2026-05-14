package com.envanter.inventory.controller;

import com.envanter.common.generic.GenericResponseWrapper;
import com.envanter.inventory.dto.CategoryDTO;
import com.envanter.inventory.dto.CategoryRequest;
import com.envanter.inventory.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Kategori yönetim controller'ı.
 *
 * <p>DIP: CategoryService arayüzüne bağlıdır.
 * Constructor Injection zorunlu.</p>
 *
 * Endpointler:
 * - GET    /api/inventory/categories           → tüm kategoriler
 * - GET    /api/inventory/categories/{id}      → tek kategori
 * - POST   /api/inventory/categories           → yeni kategori
 * - PUT    /api/inventory/categories/{id}      → güncelle
 * - DELETE /api/inventory/categories/{id}      → sil
 */
@RestController
@RequestMapping("/api/inventory/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<GenericResponseWrapper<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(GenericResponseWrapper.success(categoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseWrapper<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(GenericResponseWrapper.success(categoryService.getCategoryById(id)));
    }

    @PostMapping
    public ResponseEntity<GenericResponseWrapper<CategoryDTO>> createCategory(
            @RequestBody CategoryRequest request) {
        log.info("Yeni kategori isteği: name={}", request.getName());
        CategoryDTO created = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GenericResponseWrapper.success(created, "Kategori oluşturuldu."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseWrapper<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        log.info("Kategori güncelleme isteği: id={}", id);
        return ResponseEntity.ok(GenericResponseWrapper.success(
                categoryService.updateCategory(id, request), "Kategori güncellendi."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Kategori silme isteği: id={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
