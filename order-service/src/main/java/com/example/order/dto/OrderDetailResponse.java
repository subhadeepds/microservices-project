package com.example.order.dto;

import java.util.List;

public class OrderDetailResponse {
    private Long orderId;
    private String customerName;
    private List<ProductDetail> products;

    public OrderDetailResponse(Long orderId, String customerName, List<ProductDetail> products) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.products = products;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<ProductDetail> getProducts() { return products; }
    public void setProducts(List<ProductDetail> products) { this.products = products; }
}
