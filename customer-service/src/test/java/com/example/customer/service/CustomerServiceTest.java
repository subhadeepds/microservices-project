package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService.
 * Uses Mockito to mock the repository layer and test service logic independently.
 */
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // =========================================================
    // TEST: Get all customers
    // =========================================================
    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(List.of(
                new Customer(1L, "Alice", "alice@example.com", "9876543210"),
                new Customer(2L, "Bob", "bob@example.com", "9123456780")
        ));

        List<Customer> customers = service.getAllCustomers();

        assertEquals(2, customers.size());
        assertEquals("Alice", customers.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    // =========================================================
    // TEST: Get customer by ID (success)
    // =========================================================
    @Test
    void testGetCustomerById_Success() {
        Customer customer = new Customer(1L, "Alice", "alice@example.com", "9876543210");
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = service.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    // =========================================================
    // TEST: Get customer by ID (not found)
    // =========================================================
    @Test
    void testGetCustomerById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.getCustomerById(99L));

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).delete(any());
        verify(repository, never()).save(any());
    }

    // =========================================================
    // TEST: Create customer
    // =========================================================
    @Test
    void testCreateCustomer() {
        Customer input = new Customer("John", "john@example.com", "9000012345");
        Customer saved = new Customer(1L, "John", "john@example.com", "9000012345");

        when(repository.save(any(Customer.class))).thenReturn(saved);

        Customer result = service.createCustomer(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        verify(repository, times(1)).save(input);
    }

    // =========================================================
    // TEST: Delete customer (success)
    // =========================================================
    
    @Test
    void testDeleteCustomer_Success() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteCustomer(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    // =========================================================
    // TEST: Delete customer (not found)
    // =========================================================
    @Test
    void testDeleteCustomer_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(2L));

        verify(repository, never()).delete(any());
    }
}
