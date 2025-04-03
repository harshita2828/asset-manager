package com.example.ams.controller;

import com.example.ams.form.request.CategoryRequestDTO;
import com.example.ams.form.response.CategoryResponseDTO;
import com.example.ams.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO request) {
        try {
            if (request == null || request.getName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request: Category name cannot be null");
            }

            CategoryResponseDTO category = categoryService.saveCategory(request);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating category: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryResponseDTO> categories = categoryService.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No categories found");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching categories: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        try {
            if(id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id cannot be null");
            }
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting category : " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        try {
            if (id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id cannot be null");
            }
            CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(id);
            return ResponseEntity.ok(categoryResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> upadateCategory(@PathVariable String id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        try {
            if(id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id cannot be null");
            }

            if(categoryRequestDTO == null || categoryRequestDTO.getDescription() == null || categoryRequestDTO.getName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invlaid request: Name and Description cannot be null.");
            }

            CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(id, categoryRequestDTO);
            return ResponseEntity.ok(categoryResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating category: " + e.getMessage());
        }
    }
}
