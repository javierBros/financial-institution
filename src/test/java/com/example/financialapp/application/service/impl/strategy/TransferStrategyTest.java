package com.example.financialapp.application.service.impl.strategy;

import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.application.service.strategy.TransferStrategy;
import com.example.financialapp.domain.Product;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransferStrategyTest {

    @Mock
    private ProductRepository productRepository;

    private TransferStrategy transferStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferStrategy = new TransferStrategy();
    }

    @Test
    void testExecute_Success() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        Product sourceAccount = new Product();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(BigDecimal.valueOf(1000));
        Product destinationAccount = new Product();
        destinationAccount.setId(1L);
        destinationAccount.setBalance(BigDecimal.valueOf(500));

        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(sourceAccount))
                .thenReturn(Optional.of(destinationAccount));

        // When
        transferStrategy.execute(transaction, productRepository);

        // Then
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void testExecute_ProductNotFound() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        Product sourceAccount = new Product();
        sourceAccount.setId(1L);
        transaction.setSourceAccount(sourceAccount);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> transferStrategy.execute(transaction, productRepository));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testExecute_InsufficientBalance() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000));
        Product sourceAccount = new Product();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(BigDecimal.valueOf(500));
        Product destinationAccount = new Product();
        destinationAccount.setId(1L);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(sourceAccount))
                .thenReturn(Optional.of(destinationAccount));

        // When/Then
        assertThrows(InvalidRequestException.class, () -> transferStrategy.execute(transaction, productRepository));
        verify(productRepository, never()).save(any(Product.class));
    }
}
