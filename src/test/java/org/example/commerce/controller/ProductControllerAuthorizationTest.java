package org.example.commerce.controller;

import org.example.commerce.enums.Role;
import org.example.commerce.security.CustomUserDetails;
import org.example.commerce.security.CustomUserDetailsService;
import org.example.commerce.security.JwtService;
import org.example.commerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerAuthorizationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deleteProduct_requestByUser_return403() throws Exception {
        when(jwtService.extractEmail("fake-token")).thenReturn("quyethoang@gmail.com");
        when(jwtService.validateToken(any(), any())).thenReturn(true);

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setRole(Role.USER);

        when(customUserDetailsService.loadUserByUsername("quyethoang@gmail.com")).thenReturn(customUserDetails);
        mvc.perform(delete("/products/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    @Test
    void deleteProduct_requestByAdmin_notForbidden() throws Exception {
        when(jwtService.extractEmail("fake-token")).thenReturn("quyethoang@gmail.com");
        when(jwtService.validateToken(any(), any())).thenReturn(true);

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setRole(Role.ADMIN);

        when(customUserDetailsService.loadUserByUsername("quyethoang@gmail.com")).thenReturn(customUserDetails);
        mvc.perform(delete("/products/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());
    }
}