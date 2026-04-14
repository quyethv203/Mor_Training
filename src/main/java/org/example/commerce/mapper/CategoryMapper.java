package org.example.commerce.mapper;

import org.example.commerce.dto.request.CategoryRequest;
import org.example.commerce.dto.response.CategoryResponse;
import org.example.commerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);
}