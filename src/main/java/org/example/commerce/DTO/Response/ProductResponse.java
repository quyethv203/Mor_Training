package org.example.commerce.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer id;
    private String name;
    private String des;
    private BigDecimal price;
    private Integer stock;
    private Integer categoryId;
    private String categoryName;
}
