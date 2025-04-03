package com.example.ams.service;

import com.example.ams.form.request.CategoryRequestDTO;
import com.example.ams.form.response.CategoryResponseDTO;
import java.util.List;

public interface CategoryService {
    CategoryResponseDTO saveCategory(CategoryRequestDTO categoryRequestDTO);
    List<CategoryResponseDTO> getAllCategories();
    void deleteCategory(String id);
    CategoryResponseDTO getCategoryById(String id);
    CategoryResponseDTO updateCategory(String id, CategoryRequestDTO categoryRequestDTO);
}
