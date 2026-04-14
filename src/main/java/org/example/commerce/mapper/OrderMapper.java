package org.example.commerce.mapper;

import org.example.commerce.dto.request.OrderRequest;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "items", target = "items")
    Order toEntity(OrderRequest request);

    OrderResponse toResponse(Order order);

}
