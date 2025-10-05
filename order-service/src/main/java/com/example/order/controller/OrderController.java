package com.example.order.controller;

import com.example.order.dto.OrderDetailResponse;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderDetailResponse> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getOrderById(@PathVariable Long id) {
        return service.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return service.createOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return service.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
    }
}
