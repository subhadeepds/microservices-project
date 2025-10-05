package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.exception.BadRequestException;
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

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test: get all customers
    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(List.of(
                new Customer("Alice Johnson", "alice@example.com", "9876543210"),
                new Customer("Bob Smith", "bob@example.com", "8765432190")
        ));

        List<Customer> result = service.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    // ✅ Test: get customer by ID (found)
    @Test
    void testGetCustomerById_Found() {
        Customer customer = new Customer("John Doe", "john@example.com", "9998887777");
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = service.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(repository).findById(1L);
    }

    // ✅ Test: get customer by ID (not found)
    @Test
    void testGetCustomerById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> service.getCustomerById(1L));
    }

    // ✅ Test: create valid customer
    @Test
    void testCreateCustomer_Valid() {
        Customer newCustomer = new Customer("Charlie Brown", "charlie@example.com", "9123456789");
        when(repository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = service.createCustomer(newCustomer);

        assertNotNull(result);
        assertEquals("Charlie Brown", result.getName());
        verify(repository).save(newCustomer);
    }

    // ✅ Test: invalid customer (no email)
    @Test
    void testCreateCustomer_InvalidEmail() {
        Customer invalid = new Customer("Sam", "samemail.com", "9876543210"); // invalid email
        assertThrows(BadRequestException.class, () -> service.createCustomer(invalid));
        verify(repository, never()).save(any());
    }

    // ✅ Test: update customer success
    @Test
    void testUpdateCustomer_Success() {
        Customer existing = new Customer("Alice", "alice@gmail.com", "9876543210");
        Customer updated = new Customer("Alice Johnson", "alice.j@gmail.com", "9876500000");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Customer result = service.updateCustomer(1L, updated);

        assertEquals("Alice Johnson", result.getName());
        assertEquals("alice.j@gmail.com", result.getEmail());
        assertEquals("9876500000", result.getPhone());
        verify(repository).save(existing);
    }

    // ✅ Test: delete existing customer
    @Test
    void testDeleteCustomer_Success() {
        when(repository.existsById(2L)).thenReturn(true);
        service.deleteCustomer(2L);
        verify(repository).deleteById(2L);
    }

    // ✅ Test: delete customer not found
    @Test
    void testDeleteCustomer_NotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}
