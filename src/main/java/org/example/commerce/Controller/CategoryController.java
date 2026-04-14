package org.example.commerce.Controller;

import jakarta.validation.Valid;
import org.example.commerce.DTO.Request.CategoryRequest;
import org.example.commerce.DTO.Response.CategoryResponse;
import org.example.commerce.Entity.Category;
import org.example.commerce.Service.CategoryService;
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
    public List<CategoryResponse> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @PostMapping("")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.createCategory(request));
    }
}
