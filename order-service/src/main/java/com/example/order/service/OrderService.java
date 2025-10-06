package com.example.order.service;

import com.example.order.dto.OrderDetailResponse;
import com.example.order.dto.ProductDetail;
import com.example.order.entity.Order;
import com.example.order.exception.BadRequestException;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
     * 🟩 Reduces product stock accordingly.
     */
    @Transactional
    public Order createOrder(Order order) {
        validateOrder(order);

        // 🟩 Deduct stock for each product
        for (Map.Entry<Long, Integer> entry : order.getProductQuantities().entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();
            updateProductStock(productId, -quantity);
        }

        return repository.save(order);
    }

    /**
     * Delete an order by ID.
     * 🟩 Restores stock quantities.
     */
    @Transactional
    public void deleteOrder(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Cannot delete — order not found with id " + id));

        // 🟩 Restore stock
        for (Map.Entry<Long, Integer> entry : order.getProductQuantities().entrySet()) {
            updateProductStock(entry.getKey(), entry.getValue());
        }

        repository.deleteById(id);
    }
    
    
    
    @Transactional
    public Order updateOrder(Long id, Order updated) {
        validateOrder(updated);

        // Fetch the existing order
        Order existing = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        // 🟩 Restore stock from old order quantities
        for (Map.Entry<Long, Integer> entry : existing.getProductQuantities().entrySet()) {
            updateProductStock(entry.getKey(), entry.getValue());
        }

        // 🟨 Deduct stock for new order quantities
        for (Map.Entry<Long, Integer> entry : updated.getProductQuantities().entrySet()) {
            updateProductStock(entry.getKey(), -entry.getValue());
        }

        // 🟦 Update and save the order
        existing.setCustomerId(updated.getCustomerId());
        existing.setProductQuantities(updated.getProductQuantities());

        return repository.save(existing);
    }

    /**
     * Convert Order entity to a detailed response containing customer + products info.
     */
    private OrderDetailResponse convertToDetailedResponse(Order order) {
        String customerUrl = "http://localhost:8080/customer-service/customers/" + order.getCustomerId();
        Map<?, ?> customer = restTemplate.getForObject(customerUrl, Map.class);
        String customerName = (customer != null && customer.containsKey("name"))
                ? (String) customer.get("name")
                : "Unknown Customer";

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
     * 🟩 Helper method to call Product Service and adjust stock.
     */
    private void updateProductStock(Long productId, int change) {
        String url = "http://localhost:8080/product-service/products/" + productId + "/stock?change=" + change;
        try {
            restTemplate.put(url, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update stock for product ID " + productId + ": " + e.getMessage());
        }
    }

    /**
     * Validate order input for business rules.
     */
    private void validateOrder(Order order) {
        if (order == null) throw new BadRequestException("Order cannot be null");
        if (order.getCustomerId() == null) throw new BadRequestException("Customer ID is required");
        if (order.getProductQuantities() == null || order.getProductQuantities().isEmpty())
            throw new BadRequestException("Order must contain at least one product");

        for (Map.Entry<Long, Integer> entry : order.getProductQuantities().entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= 0)
                throw new BadRequestException("Quantity for product ID " + entry.getKey() + " must be positive");
        }
    }
}
