package org.example.commerce.mapper;

import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}
