package com.example.financialapp.application.service.impl;

import com.example.financialapp.adapters.outbound.ClientRepository;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.domain.AccountStatus;
import com.example.financialapp.domain.AccountType;
import com.example.financialapp.domain.Client;
import com.example.financialapp.domain.Product;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        // Given
        Client client = new Client();
        client.setId(1L);
        Product product = new Product();
        product.setAccountType(AccountType.SAVINGS);
        product.setBalance(BigDecimal.valueOf(1000));

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            return savedProduct;
        });

        // When
        Product createdProduct = productService.createProduct(product, 1L);

        // Then
        assertNotNull(createdProduct);
        assertEquals("5300000001", createdProduct.getAccountNumber());
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_ClientNotFound() {
        // Given
        Product product = new Product();
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(product, 1L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testCreateProduct_InvalidStatus() {
        // Given
        Client client = new Client();
        client.setId(1L);
        Product product = new Product();
        product.setAccountType(AccountType.SAVINGS);
        product.setStatus(AccountStatus.CANCELED);
        product.setBalance(new BigDecimal(-1));

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // When/Then
        assertThrows(InvalidRequestException.class, () -> productService.createProduct(product, 1L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setBalance(BigDecimal.valueOf(500));

        Product updateDetails = new Product();
        updateDetails.setStatus(AccountStatus.ACTIVE);
        updateDetails.setBalance(BigDecimal.valueOf(1000));
        updateDetails.setGmfExempt(true);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // When
        Product updatedProduct = productService.updateProduct(1L, updateDetails);

        // Then
        assertNotNull(updatedProduct);
        assertEquals(AccountStatus.ACTIVE, updatedProduct.getStatus());
        assertEquals(BigDecimal.valueOf(1000), updatedProduct.getBalance());
        assertTrue(updatedProduct.isGmfExempt());
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testDeleteProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setBalance(BigDecimal.ZERO);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_InvalidBalance() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setBalance(BigDecimal.valueOf(100));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // When/Then
        assertThrows(InvalidRequestException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).delete(product);
    }
}
