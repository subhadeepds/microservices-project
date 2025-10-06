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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *  Integration tests for CustomerController.
 * 
 * - Uses a real Spring context (no mocks)
 * - Security is DISABLED for test profile only
 * - Uses in-memory DB (H2) by default
 * - Tests full request → controller → service → repository flow
 */
@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll(); // Clean DB before each test
        repository.save(new Customer("Alice", "alice@example.com", "9876543210"));
        repository.save(new Customer("Bob", "bob@example.com", "9123456780"));
    }

    //  Test: Get all customers
    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")));
    }

    //  Test: Get single customer by ID
    @Test
    void testGetCustomerById() throws Exception {
        Customer customer = repository.save(new Customer("Charlie", "charlie@example.com", "9000012345"));

        mockMvc.perform(get("/customers/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Charlie")))
                .andExpect(jsonPath("$.email", is("charlie@example.com")));
    }

    //  Test: Add new customer
    @Test
    void testCreateCustomer() throws Exception {
        String json = """
                {
                    "name": "David",
                    "email": "david@example.com",
                    "phone": "9112233445"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("David")))
                .andExpect(jsonPath("$.id").exists());
    }

    //  Test: Update existing customer
    @Test
    void testUpdateCustomer() throws Exception {
        Customer existing = repository.save(new Customer("Eve", "eve@example.com", "9334455667"));

        String updatedJson = """
                {
                    "name": "Eve Updated",
                    "email": "eve.new@example.com",
                    "phone": "9000000000"
                }
                """;

        mockMvc.perform(put("/customers/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Eve Updated")))
                .andExpect(jsonPath("$.email", is("eve.new@example.com")));
    }

    //  Test: Delete a customer
    @Test
    void testDeleteCustomer() throws Exception {
        Customer toDelete = repository.save(new Customer("Frank", "frank@example.com", "9777888999"));

        mockMvc.perform(delete("/customers/" + toDelete.getId()))
                .andExpect(status().isOk());

        // Confirm deletion
        mockMvc.perform(get("/customers/" + toDelete.getId()))
                .andExpect(status().isNotFound());
    }
}
