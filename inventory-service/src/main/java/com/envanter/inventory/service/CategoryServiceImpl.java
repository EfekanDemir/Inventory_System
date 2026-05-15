package com.envanter.inventory.service;

import com.envanter.inventory.dto.CategoryDTO;
import com.envanter.inventory.dto.CategoryRequest;
import com.envanter.inventory.exception.ConflictException;
import com.envanter.inventory.exception.ResourceNotFoundException;
import com.envanter.inventory.exception.ValidationException;
import com.envanter.inventory.model.Category;
import com.envanter.inventory.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryService implementasyonu.
 *
 * <p>SRP: Sadece kategori iş akışını orkestre eder.
 * DIP: JdbcCategoryRepository'ye değil — somut impl'ye bağlıdır
 * (Repository interface yok çünkü tek implementasyon var — YAGNI).</p>
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // -------------------------------------------------------------------------
    // CategoryService impl
    // -------------------------------------------------------------------------

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kategori bulunamadı: id=" + id));
        return toDTO(cat);
    }

    @Override
    public CategoryDTO createCategory(CategoryRequest request) {
        // Validasyon
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Kategori adı boş olamaz.");
        }
        // Benzersizlik kontrolü
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException(
                    "Bu kategori adı zaten mevcut: " + request.getName());
        }

        Category cat = new Category();
        cat.setName(request.getName().trim());
        cat.setDescription(request.getDescription());

        Category saved = categoryRepository.save(cat);
        log.info("Yeni kategori oluşturuldu: id={}, name={}", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Güncellenecek kategori bulunamadı: id=" + id));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Kategori adı boş olamaz.");
        }

        // Ad değiştiyse çakışma kontrolü
        if (!existing.getName().equalsIgnoreCase(request.getName())) {
            categoryRepository.findByName(request.getName()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new ConflictException(
                            "Bu kategori adı başka bir kayıda ait: " + request.getName());
                }
            });
        }

        existing.setName(request.getName().trim());
        existing.setDescription(request.getDescription());

        Category updated = categoryRepository.save(existing);
        log.info("Kategori güncellendi: id={}", updated.getId());
        return toDTO(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Silinecek kategori bulunamadı: id=" + id);
        }
        categoryRepository.deleteById(id);
        log.info("Kategori silindi: id={}", id);
    }

    // -------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------

    private CategoryDTO toDTO(Category cat) {
        return new CategoryDTO(
                cat.getId(),
                cat.getName(),
                cat.getDescription(),
                cat.getCreatedAt());
    }
}
