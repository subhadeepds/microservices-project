package com.example.order.service;

import com.example.order.dto.OrderDetailResponse;
import com.example.order.dto.ProductDetail;
import com.example.order.entity.Order;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public List<OrderDetailResponse> getAllOrders() {
        return repository.findAll()
                .stream()
                .map(this::convertToDetailedResponse)
                .collect(Collectors.toList());
    }

    public OrderDetailResponse getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
        return convertToDetailedResponse(order);
    }

    public Order createOrder(Order order) {
        return repository.save(order);
    }

    public Order updateOrder(Long id, Order updated) {
        Order existing = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
        existing.setCustomerId(updated.getCustomerId());
        existing.setProductQuantities(updated.getProductQuantities());
        return repository.save(existing);
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }

    private OrderDetailResponse convertToDetailedResponse(Order order) {
        // Fetch customer
        String customerUrl = "http://localhost:8080/customer-service/customers/" + order.getCustomerId();
        Map<?, ?> customer = restTemplate.getForObject(customerUrl, Map.class);
        String customerName = (customer != null && customer.containsKey("name"))
                ? (String) customer.get("name")
                : "Unknown Customer";

        // Fetch product details
        List<ProductDetail> productDetails = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : order.getProductQuantities().entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();

            try {
                String productUrl = "http://localhost:8080/product-service/products/" + productId;
                Map<?, ?> product = restTemplate.getForObject(productUrl, Map.class);
                String productName = (product != null && product.containsKey("name"))
                        ? (String) product.get("name")
                        : "Unknown Product";
                productDetails.add(new ProductDetail(productId, productName, qty));
            } catch (Exception e) {
                productDetails.add(new ProductDetail(productId, "Unavailable Product", qty));
            }
        }

        return new OrderDetailResponse(order.getId(), customerName, productDetails);
    }
}
