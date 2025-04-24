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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDTO saveCategory(CategoryRequestDTO categoryRequestDTO) {
        try {
            if (categoryRequestDTO == null ||
                    categoryRequestDTO.getName() == null || categoryRequestDTO.getName().trim().isEmpty() ||
                    categoryRequestDTO.getDescription() == null || categoryRequestDTO.getDescription().trim().isEmpty()) {
                logger.error("Name and description are required.");
                throw new IllegalArgumentException("Invalid request: Name and Description are required.");
            }

            Optional<Category> existingCategory = categoryRepository.findByName(categoryRequestDTO.getName().trim());
            logger.info("Category retrieved with name: " + categoryRequestDTO.getName());

            if(existingCategory.isPresent()){
                logger.error("Category already exists with name: " + categoryRequestDTO.getName());
                throw new RuntimeException("Category with this name already exists.");
            }

            Category category = new Category();
            category.setName(categoryRequestDTO.getName().trim());
            category.setDescription(categoryRequestDTO.getDescription().trim());

            Category savedCategory = categoryRepository.save(category);
            logger.info("Category saved with id: " + savedCategory.getId());

            return new CategoryResponseDTO(
                    savedCategory.getId().toString(),
                    savedCategory.getName(),
                    savedCategory.getDescription()
            );
        } catch (Exception e) {
            logger.error("Error while saving category: " + e.getMessage());
            throw new RuntimeException("Error saving category: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            logger.info("Categories retrieved.");
            if (categories.isEmpty()) {
                logger.error("No categories in db.");
                throw new RuntimeException("No categories found.");
            }

            return categories.stream()
                    .map(category -> new CategoryResponseDTO(
                            category.getId().toString(),
                            category.getName(),
                            category.getDescription()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("Error while fetching categories: " + e.getMessage());
            throw new RuntimeException("Error fetching categories: " + e.getMessage());
        }
    }

    @Override
    public void deleteCategory(String id) {
        try {
            if (!categoryRepository.existsById(Long.valueOf(id))) {
                logger.error("Category not found with id: " + id);
                throw new RuntimeException("Category not found.");
            }
            categoryRepository.deleteById(Long.valueOf(id));
            logger.info("Category deleted with id: " + id);
        } catch (Exception e) {
            logger.info("Error while deleting category: " + e.getMessage());
            throw new RuntimeException("Error deleting category: " + e.getMessage());
        }
    }

    @Override
    public CategoryResponseDTO getCategoryById(String id) {
        try {
            Category category = categoryRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            return new CategoryResponseDTO(
                    category.getId().toString(),
                    category.getName(),
                    category.getDescription()
            );
        } catch (Exception e) {
            logger.error("Error while fetching category: " + e.getMessage());
            throw new RuntimeException("Error fetching category : " + e.getMessage());
        }
    }

    @Override
    public CategoryResponseDTO updateCategory(String id, CategoryRequestDTO categoryRequestDTO) {
        try {
            if(categoryRequestDTO == null ||
            categoryRequestDTO.getName() == null || categoryRequestDTO.getName().trim().isEmpty() ||
            categoryRequestDTO.getDescription() == null || categoryRequestDTO.getDescription().trim().isEmpty()) {
                logger.error("Name and description required for updating category.");
                throw new IllegalArgumentException("Invalid request: Name and Description are required.");
            }

            Category category = categoryRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Category not found."));

            category.setName(categoryRequestDTO.getName().trim());
            category.setDescription(categoryRequestDTO.getDescription().trim());

            Category updatedCategory = categoryRepository.save(category);
            logger.info("Category updated with id: " + id);

            return new CategoryResponseDTO(
                    updatedCategory.getId().toString(),
                    updatedCategory.getName(),
                    updatedCategory.getDescription()
            );
        } catch (Exception e) {
            logger.error("Error while updating category: " + e.getMessage());
            throw new RuntimeException("Error updating category: " + e.getMessage());
        }
    }
}
