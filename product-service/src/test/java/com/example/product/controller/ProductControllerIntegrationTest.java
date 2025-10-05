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
        repository.save(new Product("Laptop", "Powerful laptop", 1200.0, 10));
        repository.save(new Product("Phone", "Smartphone with AMOLED", 800.0, 15));
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(1200.0)));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = repository.save(new Product("Keyboard", "RGB mechanical keyboard", 100.0, 20));

        mockMvc.perform(get("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Keyboard")))
                .andExpect(jsonPath("$.description", is("RGB mechanical keyboard")))
                .andExpect(jsonPath("$.stock", is(20)));
    }

    @Test
    void testAddProduct() throws Exception {
        String json = """
                {
                    "name": "Monitor",
                    "description": "24-inch Full HD monitor",
                    "price": 300.0,
                    "stock": 8
                }
                """;

        mockMvc.perform(post("/products")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Monitor")))
                .andExpect(jsonPath("$.description", is("24-inch Full HD monitor")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = repository.save(new Product("Mouse", "Wireless mouse", 25.0, 30));

        mockMvc.perform(delete("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/" + product.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }
}
