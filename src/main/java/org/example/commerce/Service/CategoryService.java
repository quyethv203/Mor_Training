package org.example.commerce.Service;

import jakarta.validation.Valid;
import org.example.commerce.DTO.Request.CategoryRequest;
import org.example.commerce.DTO.Response.CategoryResponse;
import org.example.commerce.Entity.Category;
import org.example.commerce.Exception.AlreadyExistedResource;
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

    public List<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    public Category createCategory(@Valid CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AlreadyExistedResource("Category is already existed!");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }
}
