package com.example.ams.controller;

import com.example.ams.datamodels.constants.Constants;
import com.example.ams.datamodels.form.request.CategoryRequestDTO;
import com.example.ams.datamodels.form.response.APIResponse;
import com.example.ams.datamodels.form.response.CategoryResponseDTO;
import com.example.ams.service.CategoryService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    private final Constants constants;

    public CategoryController(CategoryService categoryService, Constants constants) {
        this.categoryService = categoryService;
        this.constants = constants;
    }

    @PostMapping
    public ResponseEntity<APIResponse<CategoryResponseDTO>> createCategory(@RequestBody CategoryRequestDTO request) {
        logger.info("Inside createCategory.");
        try {
            if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Category name cannot be null", null));
            }

            CategoryResponseDTO category = categoryService.saveCategory(request);
            return ResponseEntity.ok(new APIResponse<>(true, "Category created successfully", category));
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error creating category: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<CategoryResponseDTO>>> getAllCategories() {
        logger.info("Inside getAllCategories.");
        try {
            List<CategoryResponseDTO> categories = categoryService.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new APIResponse<>(true, "No categories found", null));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "Categories fetched successfully", categories));
        } catch (Exception e) {
            logger.error("Error fetching categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching categories: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCategory(@PathVariable String id) {
        logger.info("Inside deleteCategory.");
        try {
            if (ObjectUtils.isEmpty(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, constants.ID_CANNOT_BE_NULL, null));
            }
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new APIResponse<>(true, "Category deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error deleting category: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponseDTO>> getCategoryById(@PathVariable String id) {
        logger.info("Inside getCategoryById.");
        try {
            if (ObjectUtils.isEmpty(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, constants.ID_CANNOT_BE_NULL, null));
            }
            CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new APIResponse<>(true, "Category fetched successfully", categoryResponseDTO));
        } catch (Exception e) {
            logger.error("Error fetching category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching category: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponseDTO>> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("Inside updateCategory.");
        try {
            if (ObjectUtils.isEmpty(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, constants.ID_CANNOT_BE_NULL, null));
            }

            if (ObjectUtils.isEmpty(categoryRequestDTO) ||
                    ObjectUtils.isEmpty(categoryRequestDTO.getName()) ||
                    ObjectUtils.isEmpty(categoryRequestDTO.getDescription())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Name and Description cannot be null", null));
            }

            CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(id, categoryRequestDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "Category updated successfully", categoryResponseDTO));
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error updating category: " + e.getMessage(), null));
        }
    }
}
