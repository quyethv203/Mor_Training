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
        Category category_1 = new Category(1, "Đồ ăn", new ArrayList<>());
        Category category_2 = new Category(2, "Đồ gia dụng", new ArrayList<>());
        List<Category> categoryList = List.of(category_1, category_2);

        CategoryResponse res_1 = new CategoryResponse(1, "Đồ ăn");
        CategoryResponse res_2 = new CategoryResponse(2, "Đồ gia dụng");

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toResponse(category_1)).thenReturn(res_1);
        when(categoryMapper.toResponse(category_2)).thenReturn(res_2);

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
    void createCategory_existedCategory_throwAlreadyExistedResourceException() {
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
