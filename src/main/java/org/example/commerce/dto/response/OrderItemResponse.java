package org.example.commerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
