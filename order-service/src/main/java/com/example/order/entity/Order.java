package com.example.order.entity;

import jakarta.persistence.*;
import java.util.Map;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    // Map<productId, quantity>
    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Long, Integer> productQuantities;

    public Order() {}

    public Order(Long id, Long customerId, Map<Long, Integer> productQuantities) {
        this.id = id;
        this.customerId = customerId;
        this.productQuantities = productQuantities;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Map<Long, Integer> getProductQuantities() { return productQuantities; }
    public void setProductQuantities(Map<Long, Integer> productQuantities) { this.productQuantities = productQuantities; }
}
