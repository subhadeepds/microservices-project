package com.example.customer.service;

import com.example.customer.entity.Customer;
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

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(List.of(
                new Customer(1L, "Alice Johnson", "alice@example.com"),
                new Customer(2L, "Bob Smith", "bob@example.com")
        ));

        List<Customer> result = service.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = service.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(repository).findById(1L);
    }

    @Test
    void testAddCustomer() {
        Customer newCustomer = new Customer(null, "Charlie Brown", "charlie@example.com");
        Customer savedCustomer = new Customer(3L, "Charlie Brown", "charlie@example.com");

        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = service.createCustomer(newCustomer);

        assertNotNull(result.getId());
        assertEquals("Charlie Brown", result.getName());
        verify(repository).save(newCustomer);
    }

    void testDeleteCustomer() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteCustomer(5L));

        verify(repository, never()).deleteById(anyLong());
    }
}
