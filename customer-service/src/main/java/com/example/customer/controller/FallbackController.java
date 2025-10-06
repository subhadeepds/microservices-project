package com.example.customer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/customer")
    public String customerFallback() {
        return "⚠️ Customer Service is currently unavailable. Please try again later.";
    }
}
