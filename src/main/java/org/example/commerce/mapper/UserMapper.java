package org.example.commerce.mapper;

import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    RegisterResponse toRegisterResponse(User user);
}
