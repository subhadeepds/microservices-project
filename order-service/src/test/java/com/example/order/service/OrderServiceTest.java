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
        // Mock order
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setProductQuantities(Map.of(1L, 2));

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        // Mock calls to customer & product microservices
        when(restTemplate.getForObject("http://customer-service/customers/1", Map.class))
                .thenReturn(Map.of("name", "Alice Johnson"));

        when(restTemplate.getForObject("http://product-service/products/1", Map.class))
                .thenReturn(Map.of("name", "Laptop"));

        // Execute
        OrderDetailResponse response = service.getOrderById(1L);

        // Validate
        assertNotNull(response);
        assertEquals("Alice Johnson", response.getCustomerName());
        assertEquals(1, response.getProducts().size());
        assertEquals("Laptop", response.getProducts().get(0).getProductName());
        assertEquals(2, response.getProducts().get(0).getQuantity());
    }

    @Test
    void testGetOrderById_ThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getOrderById(99L));
    }
}
