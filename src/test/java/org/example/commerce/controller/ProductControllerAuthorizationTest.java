package org.example.commerce.controller;

import jakarta.transaction.Transactional;
import org.example.commerce.entity.Product;
import org.example.commerce.entity.User;
import org.example.commerce.enums.Role;
import org.example.commerce.repository.ProductRepository;
import org.example.commerce.security.CustomUserDetails;
import org.example.commerce.security.CustomUserDetailsService;
import org.example.commerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerAuthorizationTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void deleteProduct_requestByUser_return403() throws Exception {
        when(jwtService.extractEmail("fake-token")).thenReturn("quyethoang@gmail.com");
        when(jwtService.validateToken(any(), any())).thenReturn(true);

        User user = new User(1, "quyethv", "quyethoang@gmail.com", "123456", Role.USER);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        when(customUserDetailsService.loadUserByUsername("quyethoang@gmail.com")).thenReturn(customUserDetails);
        mvc.perform(delete("/products/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void deleteProduct_requestByAdmin_notForbidden() throws Exception {
        when(jwtService.extractEmail("fake-token")).thenReturn("quyethoang@gmail.com");
        when(jwtService.validateToken(any(), any())).thenReturn(true);

        User user = new User(1, "quyethv", "quyethoang@gmail.com", "123456", Role.ADMIN);

        Product product = new Product();
        product.setName("Iphone 15 promax");
        product.setDescription("Iphone 15 series");
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(25000000));

        productRepository.save(product);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        when(customUserDetailsService.loadUserByUsername("quyethoang@gmail.com")).thenReturn(customUserDetails);
        mvc.perform(delete("/products/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());
    }
}