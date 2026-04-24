package org.example.commerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commerce.enums.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private Integer id;
    private String name;
    private String email;
    private Role role;
}