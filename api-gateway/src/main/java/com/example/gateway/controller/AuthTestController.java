package com.example.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/auth/check")
    public String authCheck() {
        return "✅ You are authenticated successfully through API Gateway!";
    }
}
