package com.example.order.service;

import com.example.order.dto.OrderDetailResponse;
import com.example.order.dto.ProductDetail;
import com.example.order.entity.Order;
import com.example.order.exception.BadRequestException;
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

    /**
     * Retrieve all orders with detailed info (customer + products)
     */
    public List<OrderDetailResponse> getAllOrders() {
        return repository.findAll()
                .stream()
                .map(this::convertToDetailedResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single order by ID with full details.
     */
    public OrderDetailResponse getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
        return convertToDetailedResponse(order);
    }

    /**
     * Create a new order after validating its fields.
     */
    public Order createOrder(Order order) {
        validateOrder(order);
        return repository.save(order);
    }

    /**
     * Update an existing order.
     */
    public Order updateOrder(Long id, Order updated) {
        validateOrder(updated);

        Order existing = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        existing.setCustomerId(updated.getCustomerId());
        existing.setProductQuantities(updated.getProductQuantities());
        return repository.save(existing);
    }

    /**
     * Delete an order by ID.
     */
    public void deleteOrder(Long id) {
        if (!repository.existsById(id)) {
            throw new OrderNotFoundException("Cannot delete — order not found with id " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Convert Order entity to a detailed response containing customer + products info.
     */
    private OrderDetailResponse convertToDetailedResponse(Order order) {
        // Fetch customer details
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

    /**
     * Validate order input for business rules.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new BadRequestException("Order cannot be null");
        }
        if (order.getCustomerId() == null) {
            throw new BadRequestException("Customer ID is required");
        }
        if (order.getProductQuantities() == null || order.getProductQuantities().isEmpty()) {
            throw new BadRequestException("Order must contain at least one product");
        }
        for (Map.Entry<Long, Integer> entry : order.getProductQuantities().entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new BadRequestException("Quantity for product ID " + entry.getKey() + " must be positive");
            }
        }
    }
}
