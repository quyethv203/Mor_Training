package org.example.commerce.controller;

import org.example.commerce.dto.response.ProductResponse;
import org.example.commerce.enums.Role;
import org.example.commerce.security.CustomUserDetails;
import org.example.commerce.security.CustomUserDetailsService;
import org.example.commerce.security.JwtService;
import org.example.commerce.service.ProductService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetails customUserDetails;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void getProductWithCategory_withoutToken_return401() throws Exception {
        mvc.perform(get("/products/with-category")).andExpect(status().isUnauthorized());
    }

    @Test
    void getProductWithCategory_withToken_returnApiResponse() throws Exception {
        ProductResponse response_1 = new ProductResponse();
        response_1.setCategoryId(2);
        response_1.setId(1);
        response_1.setName("Chảo chống dính");
        response_1.setStock(7);
        response_1.setPrice(BigDecimal.valueOf(350000.00));
        response_1.setDescription("Chảo chống dinh siêu bền");
        response_1.setCategoryName("Đồ gia dụng");

        ProductResponse response_2 = new ProductResponse();
        response_2.setCategoryId(1);
        response_2.setId(2);
        response_2.setName("Cánh gà nướng");
        response_2.setStock(6);
        response_2.setPrice(BigDecimal.valueOf(25000.00));
        response_2.setDescription("Cánh gà chiên mắm");
        response_2.setCategoryName("Đồ ăn");

        List<ProductResponse> productResponses = List.of(response_1, response_2);

        when(jwtService.extractEmail("fake-token")).thenReturn("quyethoang@gmail.com");
        when(jwtService.validateToken(any(), any())).thenReturn(true);

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setRole(Role.USER);

        when(customUserDetailsService.loadUserByUsername("quyethoang@gmail.com")).thenReturn(customUserDetails);
        when(productService.getProductWithCategory()).thenReturn(productResponses);
        mvc.perform(get("/products/with-category").header("Authorization", "Bearer fake-token")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200)).andExpect(jsonPath("$.message").value("Products with category retrieved successfully")).andExpect(jsonPath("$.data.length()").value(2)).andExpect(jsonPath("$.data[0].name").value("Chảo chống dính")).andExpect(jsonPath("$.data[0].price").value(350000.00)).andExpect(jsonPath("$.data[1].name").value("Cánh gà nướng"));

        verify(productService, times(1)).getProductWithCategory();
    }


}
