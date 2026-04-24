package org.example.commerce.controller;

import org.example.commerce.dto.request.RegisterRequest;
import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.enums.Role;
import org.example.commerce.exception.AlreadyExistedResource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.json.JsonMapper;
import org.example.commerce.dto.request.LoginRequest;
import org.example.commerce.dto.response.LoginResponse;
import org.example.commerce.security.CustomUserDetailsService;
import org.example.commerce.security.JwtService;
import org.example.commerce.service.RefreshTokenService;
import org.example.commerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_passCredentials_returnLoginResponse() throws Exception {
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(36000000)
                .tokenType("Bearer")
                .build();

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        LoginRequest loginRequest = new LoginRequest("quyethoang@gmail.com", "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Login successfully"))
                .andExpect(jsonPath("$.data.accessToken").value(loginResponse.getAccessToken()))
                .andExpect(jsonPath("$.data.refreshToken").value(loginResponse.getRefreshToken()))
                .andExpect(jsonPath("$.data.expiresIn").value(loginResponse.getExpiresIn()))
                .andExpect(jsonPath("$.data.tokenType").value(loginResponse.getTokenType()));

        verify(userService, times(1)).loginUser(any(LoginRequest.class));
    }

    @Test
    void register_notExistedByEmail_returnRegisterResponse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("quyet hoang van");
        request.setEmail("quyethoang@gmail.com");
        request.setPassword("244466666");

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(1);
        registerResponse.setName("quyet hoang van");
        registerResponse.setEmail("quyethoang@gmail.com");
        registerResponse.setRole(Role.USER);

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Registered successfully!"))
                .andExpect(jsonPath("$.data.name").value("quyet hoang van"))
                .andExpect(jsonPath("$.data.email").value("quyethoang@gmail.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    void register_existedEmail_return409() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setName("quyet hoang van");
        request.setEmail("quyethoang@gmail.com");
        request.setPassword("244466666");

        when(userService.registerUser(request)).thenThrow(new AlreadyExistedResource("User already existed"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.status").value("Conflict"))
                .andExpect(jsonPath("$.message").value("User already existed"));
    }

}
