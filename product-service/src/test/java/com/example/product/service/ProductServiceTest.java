package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.exception.BadRequestException;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        when(repository.findAll()).thenReturn(List.of(
                new Product("Laptop", "Gaming Laptop", 1200.0, 10),
                new Product("Phone", "Flagship Phone", 800.0, 15)
        ));

        List<Product> result = service.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetProductById() {
        Product product = new Product("Monitor", "Full HD Display", 300.0, 5);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getProductById(1L);

        assertNotNull(result);
        assertEquals("Monitor", result.getName());
        assertEquals(300.0, result.getPrice());
        verify(repository).findById(1L);
    }

    @Test
    void testCreateProduct_Valid() {
        Product newProduct = new Product("Keyboard", "RGB Keyboard", 50.0, 8);
        Product savedProduct = new Product("Keyboard", "RGB Keyboard", 50.0, 8);

        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = service.createProduct(newProduct);

        assertNotNull(result);
        assertEquals("Keyboard", result.getName());
        verify(repository).save(newProduct);
    }

    @Test
    void testCreateProduct_InvalidPrice_ThrowsException() {
        Product invalid = new Product("Mouse", "Wireless Mouse", -20.0, 5);

        assertThrows(BadRequestException.class, () -> service.createProduct(invalid));
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateProduct_Success() {
        Product existing = new Product("Laptop", "Old model", 1000.0, 3);
        Product updated = new Product("Laptop", "Updated model", 1200.0, 5);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Product result = service.updateProduct(1L, updated);

        assertEquals("Updated model", result.getDescription());
        assertEquals(1200.0, result.getPrice());
        assertEquals(5, result.getStock());
        verify(repository).save(existing);
    }

    @Test
    void testDeleteProduct_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteProduct(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsException() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(ProductNotFoundException.class, () -> service.deleteProduct(99L));
        verify(repository, never()).deleteById(any());
    }
}
