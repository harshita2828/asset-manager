package com.example.ams.service.impl;

import com.example.ams.dao.CategoryRepository;
import com.example.ams.entities.Category;
import com.example.ams.form.request.CategoryRequestDTO;
import com.example.ams.form.response.CategoryResponseDTO;
import com.example.ams.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
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
                throw new IllegalArgumentException("Invalid request: Name and Description are required.");
            }

            Optional<Category> existingCategory = categoryRepository.findByName(categoryRequestDTO.getName().trim());
            if(existingCategory.isPresent()){
                throw new RuntimeException("Category with this name already exists.");
            }

            Category category = new Category();
            category.setName(categoryRequestDTO.getName().trim());
            category.setDescription(categoryRequestDTO.getDescription().trim());

            Category savedCategory = categoryRepository.save(category);
            System.out.println("Saved Category: " + savedCategory.getName());

            return new CategoryResponseDTO(
                    savedCategory.getId().toString(),
                    savedCategory.getName(),
                    savedCategory.getDescription()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving category: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();

            if (categories.isEmpty()) {
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
            throw new RuntimeException("Error fetching categories: " + e.getMessage());
        }
    }

    @Override
    public void deleteCategory(String id) {
        try {
            if (!categoryRepository.existsById(Long.valueOf(id))) {
                throw new RuntimeException("Category not found.");
            }
            categoryRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
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
            throw new RuntimeException("Error fetching category : " + e.getMessage());
        }
    }

    @Override
    public CategoryResponseDTO updateCategory(String id, CategoryRequestDTO categoryRequestDTO) {
        try {
            if(categoryRequestDTO == null ||
            categoryRequestDTO.getName() == null || categoryRequestDTO.getName().trim().isEmpty() ||
            categoryRequestDTO.getDescription() == null || categoryRequestDTO.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid request: Name and Description are required.");
            }

            Category category = categoryRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Category not found."));

            category.setName(categoryRequestDTO.getName().trim());
            category.setDescription(categoryRequestDTO.getDescription().trim());

            Category updatedCategory = categoryRepository.save(category);

            return new CategoryResponseDTO(
                    updatedCategory.getId().toString(),
                    updatedCategory.getName(),
                    updatedCategory.getDescription()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error updating category: " + e.getMessage());
        }
    }
}
