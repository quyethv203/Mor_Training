package org.example.commerce.controller;
import jakarta.validation.Valid;
import org.example.commerce.dto.request.OrderRequest;
import org.example.commerce.dto.response.ApiResponse;
import org.example.commerce.dto.response.OrderDetailResponse;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrder();
        ApiResponse<List<OrderResponse>> response = ApiResponse.<List<OrderResponse>>builder()
                .code((HttpStatus.OK.value()))
                .message("Order retrieved successfully")
                .data(orderResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getDetailOrder(@PathVariable Integer orderId) {
        OrderDetailResponse orderDetailResponse = orderService.getDetailOrder(orderId);
        ApiResponse<OrderDetailResponse> response = ApiResponse.<OrderDetailResponse>builder()
                .code((HttpStatus.OK.value()))
                .message("Order detail retrieved successfully")
                .data(orderDetailResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
