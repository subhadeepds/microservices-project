package com.example.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/orders")
    public Mono<String> orderFallback() {
        return Mono.just("⚠️ Order Service temporarily unavailable");
    }

    @GetMapping("/fallback/products")
    public Mono<String> productFallback() {
        return Mono.just("⚠️ Product Service temporarily unavailable");
    }

    @GetMapping("/fallback/customers")
    public Mono<String> customerFallback() {
        return Mono.just("⚠️ Customer Service temporarily unavailable");
    }
}
