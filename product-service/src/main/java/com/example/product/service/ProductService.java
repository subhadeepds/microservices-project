package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.exception.BadRequestException;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all products.
     */
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    /**
     * Get a product by ID.
     */
    public Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
    }

    /**
     * Create a new product (with validation).
     */
    public Product createProduct(Product product) {
        validateProduct(product);
        return repository.save(product);
    }

    /**
     * Update an existing product (with validation).
     */
    public Product updateProduct(Long id, Product updated) {
        validateProduct(updated);

        Product existing = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());

        return repository.save(existing);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Cannot delete — product not found with id " + id);
        }
        repository.deleteById(id);
    }
    
    
    
    
    public void decreaseStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        if (product.getStock() < quantity) {
            throw new BadRequestException("Not enough stock for product ID " + productId);
        }
        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }

    /**
     * Increase stock when an order is deleted (restock).
     */
    public void increaseStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.setStock(product.getStock() + quantity);
        repository.save(product);
    }
    
    
    
    
    

    /**
     * Validate the product input before saving.
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new BadRequestException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BadRequestException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new BadRequestException("Product price must be positive");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BadRequestException("Product stock cannot be negative");
        }
    }
}
