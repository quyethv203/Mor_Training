package org.example.commerce.mapper;

import org.example.commerce.dto.request.ProductRequest;
import org.example.commerce.dto.response.ProductResponse;
import org.example.commerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(source = "categoryId", target = "category.id")
    Product toEntity(ProductRequest request);

    @Mapping(target = "category", ignore = true)
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product existingProduct);
}
