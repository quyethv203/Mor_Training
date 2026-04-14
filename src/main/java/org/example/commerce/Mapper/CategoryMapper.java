package org.example.commerce.Mapper;

import org.example.commerce.DTO.Request.CategoryRequest;
import org.example.commerce.DTO.Response.CategoryResponse;
import org.example.commerce.Entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);
}