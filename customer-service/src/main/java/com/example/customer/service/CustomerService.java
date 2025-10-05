package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + id));
    }

    public Customer createCustomer(Customer customer) {
        return repository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = getCustomerById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        return repository.save(existing);
    }

    public void deleteCustomer(Long id) {
        Customer existing = getCustomerById(id);
        repository.delete(existing);
    }
}
