package com.example.customer.controller;

import com.example.customer.entity.Customer;
import com.example.customer.repository.CustomerRepository;
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
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.save(new Customer(null, "Alice Johnson", "alice@example.com"));
        repository.save(new Customer(null, "Bob Smith", "bob@example.com"));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")));
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer customer = repository.save(new Customer(null, "Charlie Brown", "charlie@example.com"));

        mockMvc.perform(get("/customers/" + customer.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Charlie Brown")));
    }

    @Test
    void testAddCustomer() throws Exception {
        String json = """
                {
                    "name": "David Miller",
                    "email": "david@example.com"
                }
                """;

        mockMvc.perform(post("/customers")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("David Miller")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testDeleteCustomer() throws Exception {
        Customer customer = repository.save(new Customer(null, "Eve Carter", "eve@example.com"));

        mockMvc.perform(delete("/customers/" + customer.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/" + customer.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }
}
