package org.example.commerce.Service;

import jakarta.validation.Valid;
import org.example.commerce.DTO.Request.CategoryRequest;
import org.example.commerce.Entity.Category;
import org.example.commerce.Mapper.CategoryMapper;
import org.example.commerce.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Category createCategory(@Valid CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }
}
