package org.example.commerce.Mapper;

import org.example.commerce.DTO.Request.ProductRequest;
import org.example.commerce.DTO.Response.ProductResponse;
import org.example.commerce.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(source = "category_id", target = "category.id")
    Product toEntity(ProductRequest request);

    void updateProductFromRequest(ProductRequest request, @MappingTarget Product existedProduct);
}
