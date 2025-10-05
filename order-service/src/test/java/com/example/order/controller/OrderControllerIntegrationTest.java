package com.example.order.controller;

import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testCreateAndRetrieveOrder() throws Exception {
        // Create order JSON
        String orderJson = """
                {
                    "customerId": 1,
                    "productQuantities": { "1": 2, "2": 3 }
                }
                """;

        // 🔐 Add valid Basic Auth credentials here
        mockMvc.perform(post("/orders")
                        .with(httpBasic("admin", "password"))   // ✅ use same creds as your app
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        // Fetch all orders
        mockMvc.perform(get("/orders")
                        .with(httpBasic("admin", "password")))   // ✅ same credentials
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
