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
import org.example.commerce.enums.OrderStatus;
import org.example.commerce.exception.OutOfStockException;
import org.example.commerce.exception.ResourceNotFoundException;
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
        Order order1 = new Order(1, BigDecimal.valueOf(200000), OrderStatus.UNPAID, new ArrayList<>());
        Order order2 = new Order(2, BigDecimal.valueOf(25000), OrderStatus.UNPAID, new ArrayList<>());
        List<Order> orders = List.of(order1, order2);

        OrderResponse response1 = new OrderResponse(1, BigDecimal.valueOf(200000), OrderStatus.UNPAID.name());
        OrderResponse response2 = new OrderResponse(2, BigDecimal.valueOf(25000), OrderStatus.UNPAID.name());

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toResponse(order1)).thenReturn(response1);
        when(orderMapper.toResponse(order2)).thenReturn(response2);

        List<OrderResponse> results = orderService.getAllOrder();
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(BigDecimal.valueOf(200000), results.get(0).getTotalAmount());
        assertEquals("UNPAID", results.get(1).getStatus());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void createOrder_validRequest_returnOrderResponse() {
        OrderItemRequest itemRequest1 = new OrderItemRequest(1, 1);
        OrderItemRequest itemRequest2 = new OrderItemRequest(2, 2);
        OrderRequest orderRequest = new OrderRequest(List.of(itemRequest1, itemRequest2));

        Category category1 = new Category(1, "Đồ công nghệ", new ArrayList<>());
        Category category2 = new Category(2, "Đồ ăn", new ArrayList<>());

        Product product1 = new Product(1, "Iphone 15", "Iphone 15 series", BigDecimal.valueOf(25000000), 10, category1);
        Product product2 = new Product(2, "Cánh gà nướng", "Cánh gà nướng siêu ngon", BigDecimal.valueOf(25000), 5, category2);

        Order order = new Order();

        BigDecimal totalAmount = BigDecimal.ZERO;

        when(productRepository.findById(itemRequest1.getProductId())).thenReturn(Optional.of(product1));
        when(productRepository.findById(itemRequest2.getProductId())).thenReturn(Optional.of(product2));

        product1.setStock(product1.getStock() - itemRequest1.getQuantity());
        product2.setStock(product2.getStock() - itemRequest2.getQuantity());

        OrderItem orderItem1 = new OrderItem(1, product1, order, 1, product1.getPrice());
        OrderItem orderItem2 = new OrderItem(2, product2, order, 2, product2.getPrice());
        List<OrderItem> orderItemList = List.of(orderItem1, orderItem2);

        totalAmount = totalAmount.add(orderItem1.getUnitPrice().multiply(BigDecimal.valueOf(orderItem1.getQuantity())));
        totalAmount = totalAmount.add(orderItem2.getUnitPrice().multiply(BigDecimal.valueOf(orderItem2.getQuantity())));

        order.setId(1);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.UNPAID);
        order.setItems(orderItemList);
        when(orderRepository.save(any(Order.class))).thenReturn(order);


        OrderResponse orderResponse = new OrderResponse(order.getId(), order.getTotalAmount(), order.getStatus().name());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);
        assertEquals(totalAmount, result.getTotalAmount());
        assertEquals("UNPAID", result.getStatus());

        verify(productRepository, times(1)).findById(product1.getId());
        verify(productRepository, times(1)).findById(product2.getId());
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

        Order existingOrder = new Order(1, BigDecimal.valueOf(25000000), OrderStatus.UNPAID, List.of(orderItem));

        when(orderRepository.findDetailById(orderId)).thenReturn(Optional.of(existingOrder));

        BigDecimal subTotal = existingOrder.getItems().get(0).getUnitPrice().multiply(BigDecimal.valueOf(existingOrder.getItems().get(0).getQuantity()));
        OrderItemResponse orderItemResponse = new OrderItemResponse(existingOrder.getItems().get(0).getProduct().getId(), existingOrder.getItems().get(0).getQuantity(), existingOrder.getItems().get(0).getUnitPrice(), subTotal);

        OrderDetailResponse detail = new OrderDetailResponse(existingOrder.getId(), existingOrder.getTotalAmount(), existingOrder.getStatus().name(), List.of(orderItemResponse));

        OrderDetailResponse result = orderService.getDetailOrder(orderId);
        assertEquals(detail, result);
        assertEquals(detail.getItems(), result.getItems());
        assertEquals(detail.getTotalAmount(), result.getTotalAmount());

        verify(orderRepository, times(1)).findDetailById(orderId);
    }

    @Test
    void getDetailOrder_notFound_throwResourceNotFoundException() {
        when(orderRepository.findDetailById(99)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getDetailOrder(99)
        );
    }
}
