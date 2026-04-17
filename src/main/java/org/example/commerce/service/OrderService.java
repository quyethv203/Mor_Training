package org.example.commerce.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.commerce.dto.request.OrderItemRequest;
import org.example.commerce.dto.request.OrderRequest;
import org.example.commerce.dto.response.OrderDetailResponse;
import org.example.commerce.dto.response.OrderItemResponse;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.entity.Order;
import org.example.commerce.entity.OrderItem;
import org.example.commerce.entity.Product;
import org.example.commerce.exception.ResourceNotFoundException;
import org.example.commerce.mapper.OrderMapper;
import org.example.commerce.repository.OrderRepository;
import org.example.commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
    }


    public List<OrderResponse> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse createOrder( OrderRequest request) {
        Order order = new Order();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Out of stock: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            System.out.println("Product Price: " + product.getPrice());

            OrderItem item = new OrderItem();
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order);
            item.setProduct(product);

            BigDecimal subTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subTotal);

            productRepository.save(product);
            orderItemList.add(item);
        }
        order.setTotalAmount(totalAmount);
        order.setStatus("UNPAID");
        order.setItems(orderItemList);
        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    public OrderDetailResponse getDetailOrder(Integer orderId) {
        Order order = orderRepository.findDetailById(orderId);
        List<OrderItemResponse> orderItemResponses = order.getItems().stream()
                .map(orderItem -> {
                    OrderItemResponse orderItemResponse = new OrderItemResponse();
                    orderItemResponse.setProductId(orderItem.getProduct().getId());
                    orderItemResponse.setUnitPrice(orderItem.getUnitPrice());
                    orderItemResponse.setQuantity(orderItem.getQuantity());
                    orderItemResponse.setSubTotal(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
                    return orderItemResponse;
                })
                .toList();
        return new OrderDetailResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                orderItemResponses
        );
    }
}
