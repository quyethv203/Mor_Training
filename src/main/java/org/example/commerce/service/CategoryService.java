package org.example.commerce.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.commerce.dto.request.CategoryRequest;
import org.example.commerce.dto.response.CategoryResponse;
import org.example.commerce.entity.Category;
import org.example.commerce.exception.AlreadyExistedResource;
import org.example.commerce.mapper.CategoryMapper;
import org.example.commerce.repository.CategoryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public List<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AlreadyExistedResource("Category name already exists!");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
