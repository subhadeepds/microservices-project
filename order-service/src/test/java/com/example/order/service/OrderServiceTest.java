package com.example.order.service;

import com.example.order.dto.OrderDetailResponse;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrderById_ReturnsDetailedResponse() {
        // Mock an order entity
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setProductQuantities(Map.of(1L, 2));

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // Mock external calls to customer & product services
        when(restTemplate.getForObject("http://localhost:8080/customer-service/customers/1", Map.class))
                .thenReturn(Map.of("name", "Alice Johnson"));

        when(restTemplate.getForObject("http://localhost:8080/product-service/products/1", Map.class))
                .thenReturn(Map.of("name", "Laptop"));

        OrderDetailResponse response = service.getOrderById(1L);

        assertNotNull(response);
        assertEquals("Alice Johnson", response.getCustomerName());
        assertEquals("Laptop", response.getProducts().get(0).getProductName());
        assertEquals(2, response.getProducts().get(0).getQuantity());
    }

    @Test
    void testGetOrderById_ThrowsWhenNotFound() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getOrderById(5L));
    }
}
