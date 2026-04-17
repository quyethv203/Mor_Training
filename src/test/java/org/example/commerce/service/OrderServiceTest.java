package org.example.commerce.service;

import org.example.commerce.dto.request.OrderItemRequest;
import org.example.commerce.dto.request.OrderRequest;
import org.example.commerce.dto.response.OrderDetailResponse;
import org.example.commerce.dto.response.OrderItemResponse;
import org.example.commerce.dto.response.OrderResponse;
import org.example.commerce.entity.Category;
import org.example.commerce.entity.Order;
import org.example.commerce.entity.OrderItem;
import org.example.commerce.entity.Product;
import org.example.commerce.exception.OutOfStockException;
import org.example.commerce.mapper.OrderMapper;
import org.example.commerce.repository.OrderRepository;
import org.example.commerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrder_validRequest_returnListOrderResponse() {
        Order order_1 = new Order(1, BigDecimal.valueOf(200000), "UNPAID", new ArrayList<>());
        Order order_2 = new Order(2, BigDecimal.valueOf(25000), "UNPAID", new ArrayList<>());
        List<Order> orders = List.of(order_1, order_2);

        OrderResponse response_1 = new OrderResponse(1, BigDecimal.valueOf(200000), "UNPAID");
        OrderResponse response_2 = new OrderResponse(2, BigDecimal.valueOf(25000), "UNPAID");

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toResponse(order_1)).thenReturn(response_1);
        when(orderMapper.toResponse(order_2)).thenReturn(response_2);

        List<OrderResponse> results = orderService.getAllOrder();
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(BigDecimal.valueOf(200000), results.get(0).getTotalAmount());
        assertEquals("UNPAID", results.get(1).getStatus());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void createOrder_validRequest_returnOrderResponse() {
        OrderItemRequest itemRequest_1 = new OrderItemRequest(1, 1);
        OrderItemRequest itemRequest_2 = new OrderItemRequest(2, 2);
        OrderRequest orderRequest = new OrderRequest(List.of(itemRequest_1, itemRequest_2));

        Category category_1 = new Category(1, "Đồ công nghệ", new ArrayList<>());
        Category category_2 = new Category(2, "Đồ ăn", new ArrayList<>());

        Product product_1 = new Product(1, "Iphone 15", "Iphone 15 series", BigDecimal.valueOf(25000000), 10, category_1);
        Product product_2 = new Product(2, "Cánh gà nướng", "Cánh gà nướng siêu ngon", BigDecimal.valueOf(25000), 5, category_2);

        Order order = new Order();

        BigDecimal totalAmount = BigDecimal.ZERO;

        when(productRepository.findById(itemRequest_1.getProductId())).thenReturn(Optional.of(product_1));
        when(productRepository.findById(itemRequest_2.getProductId())).thenReturn(Optional.of(product_2));

        product_1.setStock(product_1.getStock() - itemRequest_1.getQuantity());
        product_2.setStock(product_2.getStock() - itemRequest_2.getQuantity());

        OrderItem orderItem_1 = new OrderItem(1, product_1, order, 1, product_1.getPrice());
        OrderItem orderItem_2 = new OrderItem(2, product_2, order, 2, product_2.getPrice());
        List<OrderItem> orderItemList = List.of(orderItem_1, orderItem_2);

        totalAmount = totalAmount.add(orderItem_1.getUnitPrice().multiply(BigDecimal.valueOf(orderItem_1.getQuantity())));
        totalAmount = totalAmount.add(orderItem_2.getUnitPrice().multiply(BigDecimal.valueOf(orderItem_2.getQuantity())));

        when(productRepository.save(product_1)).thenReturn(product_1);
        when(productRepository.save(product_2)).thenReturn(product_2);

        order.setId(1);
        order.setTotalAmount(totalAmount);
        order.setStatus("UNPAID");
        order.setItems(orderItemList);
        when(orderRepository.save(any(Order.class))).thenReturn(order);


        OrderResponse orderResponse = new OrderResponse(order.getId(), order.getTotalAmount(), order.getStatus());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);
        assertEquals(totalAmount, result.getTotalAmount());
        assertEquals("UNPAID", result.getStatus());

        verify(productRepository, times(1)).findById(product_1.getId());
        verify(productRepository, times(1)).findById(product_2.getId());
        verify(productRepository, times(1)).save(product_1);
        verify(productRepository, times(1)).save(product_2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_outOfStock_throwOutOfStockException() {
        OrderItemRequest itemRequest = new OrderItemRequest(1, 15);
        OrderRequest orderRequest = new OrderRequest(List.of(itemRequest));

        Category category = new Category(1, "Đồ công nghệ", new ArrayList<>());

        Product product = new Product(1, "Iphone 15", "Iphone 15 series", BigDecimal.valueOf(25000000), 10, category);

        when(productRepository.findById(itemRequest.getProductId())).thenReturn(Optional.of(product));

        OutOfStockException exception = assertThrows(
                OutOfStockException.class,
                () -> orderService.createOrder(orderRequest)
        );

        verify(productRepository, times(1)).findById(itemRequest.getProductId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getDetailOrder_existingOrder_returnOrderDetailResponse() {
        Integer orderId = 1;

        Order order = new Order();
        Category category = new Category(1, "Đồ công nghệ", new ArrayList<>());
        Product product = new Product(1, "Iphone 15", "Iphone 15 series", BigDecimal.valueOf(25000000), 10, category);
        OrderItem orderItem = new OrderItem(1, product, order, 1, product.getPrice());

        Order existingOrder = new Order(1, BigDecimal.valueOf(25000000), "UNPAID", List.of(orderItem));

        when(orderRepository.findDetailById(orderId)).thenReturn(existingOrder);

        BigDecimal subTotal = existingOrder.getItems().get(0).getUnitPrice().multiply(BigDecimal.valueOf(existingOrder.getItems().get(0).getQuantity()));
        OrderItemResponse orderItemResponse = new OrderItemResponse(existingOrder.getItems().get(0).getProduct().getId(), existingOrder.getItems().get(0).getQuantity(), existingOrder.getItems().get(0).getUnitPrice(), subTotal);

        OrderDetailResponse detail = new OrderDetailResponse(existingOrder.getId(), existingOrder.getTotalAmount(), existingOrder.getStatus(), List.of(orderItemResponse));

        OrderDetailResponse result = orderService.getDetailOrder(orderId);
        assertEquals(detail, result);
        assertEquals(detail.getItems(), result.getItems());
        assertEquals(detail.getTotalAmount(), result.getTotalAmount());

        verify(orderRepository, times(1)).findDetailById(orderId);

    }
}
