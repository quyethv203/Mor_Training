package org.example.commerce.controller;

import jakarta.validation.Valid;
import org.example.commerce.dto.request.OrderRequest;
import org.example.commerce.dto.response.ApiResponse;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrder();
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
