package com.example.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/orders")
    public String orderFallback() {
        return "⚠️ Order Service is temporarily unavailable. Please try again later.";
    }
}
