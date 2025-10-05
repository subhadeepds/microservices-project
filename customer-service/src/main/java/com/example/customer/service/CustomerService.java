package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.exception.BadRequestException;
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

    // ✅ Renamed method (was addCustomer)
    public Customer createCustomer(Customer customer) {
        validateCustomer(customer);
        return repository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updated) {
        validateCustomer(updated);

        Customer existing = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + id));

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());

        return repository.save(existing);
    }

    public void deleteCustomer(Long id) {
        if (!repository.existsById(id)) {
            throw new CustomerNotFoundException("Cannot delete — customer not found with id " + id);
        }
        repository.deleteById(id);
    }

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new BadRequestException("Customer cannot be null");
        }
        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new BadRequestException("Customer name cannot be empty");
        }
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new BadRequestException("Invalid email address");
        }
        if (customer.getPhone() == null || customer.getPhone().length() < 10) {
            throw new BadRequestException("Invalid phone number");
        }
    }
}
