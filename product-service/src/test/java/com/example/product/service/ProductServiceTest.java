package com.example.product.service;

import com.example.product.entity.Product;
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
                new Product(1L, "Laptop", 1200.0),
                new Product(2L, "Phone", 800.0)
        ));

        List<Product> result = service.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetProductById() {
        Product product = new Product(1L, "Monitor", 300.0);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getProductById(1L);

        assertNotNull(result);
        assertEquals("Monitor", result.getName());
        verify(repository).findById(1L);
    }

    @Test
    void testAddProduct() {
        Product newProduct = new Product(null, "Keyboard", 50.0);
        Product savedProduct = new Product(5L, "Keyboard", 50.0);

        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = service.createProduct(newProduct);

        assertNotNull(result.getId());
        assertEquals("Keyboard", result.getName());
        verify(repository).save(newProduct);
    }

    @Test
    void testDeleteProduct() {
        Product existing = new Product(1L, "Mouse", 25.0);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.deleteProduct(1L);

        verify(repository).delete(existing);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.deleteProduct(99L));
        verify(repository, never()).delete(any());
    }
}
