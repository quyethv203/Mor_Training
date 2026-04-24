package org.example.commerce.service;

import org.example.commerce.dto.request.CategoryRequest;
import org.example.commerce.dto.response.CategoryResponse;
import org.example.commerce.entity.Category;
import org.example.commerce.exception.AlreadyExistedResource;
import org.example.commerce.mapper.CategoryMapper;
import org.example.commerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategory_validRequest_returnListCategoryResponse() {
        Category category1 = new Category(1, "Đồ ăn", new ArrayList<>());
        Category category2 = new Category(2, "Đồ gia dụng", new ArrayList<>());
        List<Category> categoryList = List.of(category1, category2);

        CategoryResponse res1 = new CategoryResponse(1, "Đồ ăn");
        CategoryResponse res2 = new CategoryResponse(2, "Đồ gia dụng");

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toResponse(category1)).thenReturn(res1);
        when(categoryMapper.toResponse(category2)).thenReturn(res2);

        List<CategoryResponse> result = categoryService.getAllCategory();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Đồ gia dụng", result.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void createCategory_validRequest_returnCategoryResponse() {
        CategoryRequest request = new CategoryRequest("Đồ công nghệ");
        when(categoryRepository.existsByName(request.getName())).thenReturn(false);

        Category category = new Category(3, "Đồ công nghệ", new ArrayList<>());
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponse response = new CategoryResponse(3, "Đồ công nghệ");
        when(categoryMapper.toResponse(category)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);
        assertNotNull(result);
        assertEquals(request.getName(), result.getName());

        verify(categoryRepository, times(1)).existsByName(request.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategory_existingCategory_throwAlreadyExistedResourceException() {
        CategoryRequest request = new CategoryRequest("Đồ công nghệ");

        when(categoryRepository.existsByName(request.getName())).thenReturn(true);

        AlreadyExistedResource exception = assertThrows(
                AlreadyExistedResource.class,
                () -> categoryService.createCategory(request)
        );

        assertEquals("Category name already exists!", exception.getMessage());

        verify(categoryRepository, times(1)).existsByName(request.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

}
