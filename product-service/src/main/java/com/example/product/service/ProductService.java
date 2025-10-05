package com.example.product.service;

import com.example.product.entity.Product;
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

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product existing = getProductById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        return repository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        repository.delete(existing);
    }
}
