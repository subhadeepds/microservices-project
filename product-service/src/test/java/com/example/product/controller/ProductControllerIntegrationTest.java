package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.save(new Product(null, "Laptop", 1200.0));
        repository.save(new Product(null, "Phone", 800.0));
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Laptop")));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = repository.save(new Product(null, "Keyboard", 100.0));

        mockMvc.perform(get("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Keyboard")));
    }

    @Test
    void testAddProduct() throws Exception {
        String json = """
                {
                    "name": "Monitor",
                    "price": 300.0
                }
                """;

        mockMvc.perform(post("/products")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Monitor")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = repository.save(new Product(null, "Mouse", 25.0));

        mockMvc.perform(delete("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }
}
