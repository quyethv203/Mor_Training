package org.example.commerce.controller;

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
import org.springframework.boot.test.context.SpringBootTest;
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
}
