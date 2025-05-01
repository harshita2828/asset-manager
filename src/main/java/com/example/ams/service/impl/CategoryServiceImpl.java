package com.example.ams.service.impl;

import com.example.ams.dao.CategoryRepository;
import com.example.ams.entities.Category;
import com.example.ams.form.request.CategoryRequestDTO;
import com.example.ams.form.response.CategoryResponseDTO;
import com.example.ams.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDTO saveCategory(CategoryRequestDTO dto) {
        validateRequest(dto);

        String name = dto.getName().trim();
        String description = dto.getDescription().trim();

        if (categoryRepository.findByName(name).isPresent()) {
            logger.warn("Category already exists with name: {}", name);
            throw new RuntimeException("Category with this name already exists.");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        Category saved = categoryRepository.save(category);
        logger.info("Category saved with id: {}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        logger.info("Fetched {} categories from DB", categories.size());

        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(String id) {
        Long categoryId = parseId(id);
        if (!categoryRepository.existsById(categoryId)) {
            logger.warn("Category not found with id: {}", id);
            throw new RuntimeException("Category not found.");
        }

        categoryRepository.deleteById(categoryId);
        logger.info("Category deleted with id: {}", id);
    }

    @Override
    public CategoryResponseDTO getCategoryById(String id) {
        Long categoryId = parseId(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.warn("Category not found with id: {}", id);
                    return new RuntimeException("Category not found.");
                });

        return mapToDTO(category);
    }

    @Override
    public CategoryResponseDTO updateCategory(String id, CategoryRequestDTO dto) {
        validateRequest(dto);

        Long categoryId = parseId(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.warn("Category not found for update, id: {}", id);
                    return new RuntimeException("Category not found.");
                });

        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription().trim());

        Category updated = categoryRepository.save(category);
        logger.info("Category updated with id: {}", id);

        return mapToDTO(updated);
    }

    private void validateRequest(CategoryRequestDTO dto) {
        if (dto == null ||
                dto.getName() == null || dto.getName().trim().isEmpty() ||
                dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            logger.error("Invalid category request: Name and Description are required.");
            throw new IllegalArgumentException("Name and Description are required.");
        }
    }

    private Long parseId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException ex) {
            logger.error("Invalid category ID format: {}", id);
            throw new IllegalArgumentException("Invalid category ID.");
        }
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId().toString(),
                category.getName(),
                category.getDescription()
        );
    }
}
