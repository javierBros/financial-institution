package com.example.financialapp.application.service.impl;

import com.example.financialapp.adapters.inbound.ProductController;
import com.example.financialapp.application.service.ProductService;
import com.example.financialapp.domain.Product;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        // Given
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        // When
        ResponseEntity<List<Product>> response = productController.getAllProducts();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() {
        // Given
        Product product = new Product();
        product.setId(1L);
        when(productService.getProductById(anyLong())).thenReturn(Optional.of(product));

        // When
        ResponseEntity<Product> response = productController.getProductById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        // Given
        when(productService.getProductById(anyLong())).thenThrow(new ResourceNotFoundException("Product not found with id 1"));

        // When/Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            productController.getProductById(1L);
        });

        // Then
        assertEquals("Product not found with id 1", thrown.getMessage());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testCreateProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);
        when(productService.createProduct(any(Product.class), anyLong())).thenReturn(product);

        // When
        ResponseEntity<Product> response = productController.createProduct(product, 1L);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(productService, times(1)).createProduct(product, 1L);
    }

    @Test
    void testCreateProductError() {
        // Given
        when(productService.createProduct(any(Product.class), anyLong())).thenThrow(new RuntimeException("Error creating product"));

        // When
        ResponseEntity<Product> response = productController.createProduct(new Product(), 1L);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService, times(1)).createProduct(any(Product.class), anyLong());
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(product);

        // When
        ResponseEntity<Product> response = productController.updateProduct(1L, product);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(productService, times(1)).updateProduct(1L, product);
    }

    @Test
    void testUpdateProductNotFound() {
        // Given
        when(productService.updateProduct(anyLong(), any(Product.class))).thenThrow(new ResourceNotFoundException("Product not found"));

        // When
        ResponseEntity<Product> response = productController.updateProduct(1L, new Product());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, times(1)).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    void testUpdateProductError() {
        // Given
        when(productService.updateProduct(anyLong(), any(Product.class))).thenThrow(new RuntimeException("Error updating product"));

        // When
        ResponseEntity<Product> response = productController.updateProduct(1L, new Product());

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService, times(1)).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        // When
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        // Given
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(anyLong());

        // When
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductError() {
        // Given
        doThrow(new RuntimeException("Error deleting product")).when(productService).deleteProduct(anyLong());

        // When
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }
}
