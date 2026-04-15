package org.example.commerce.mapper;

import org.example.commerce.dto.response.LoginResponse;
import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    RegisterResponse toRegisterResponse(User user);

    @Mapping(source = "token", target = "accessToken")
    LoginResponse toLoginResponse(String token);
}
