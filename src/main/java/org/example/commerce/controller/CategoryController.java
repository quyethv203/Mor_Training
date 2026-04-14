package org.example.commerce.controller;

import jakarta.validation.Valid;
import org.example.commerce.dto.request.CategoryRequest;
import org.example.commerce.dto.response.ApiResponse;
import org.example.commerce.dto.response.CategoryResponse;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.entity.Category;
import org.example.commerce.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategory() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategory();
        ApiResponse<List<CategoryResponse>> response = ApiResponse.<List<CategoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .data(categoryResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse categoryResponse = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .data(categoryResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
