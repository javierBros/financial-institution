package com.example.financialapp.application.service.impl.strategy;

import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.application.service.strategy.WithdrawalStrategy;
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

class WithdrawalStrategyTest {

    @Mock
    private ProductRepository productRepository;

    private WithdrawalStrategy withdrawalStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        withdrawalStrategy = new WithdrawalStrategy();
    }

    @Test
    void testExecute_Success() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(-100));
        Product account = new Product();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        transaction.setSourceAccount(account);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // When
        withdrawalStrategy.execute(transaction, productRepository);

        // Then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testExecute_ProductNotFound() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(-100));
        Product account = new Product();
        account.setId(1L);
        transaction.setSourceAccount(account);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> withdrawalStrategy.execute(transaction, productRepository));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testExecute_InsufficientBalance() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(-1000));
        Product account = new Product();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(500));
        transaction.setSourceAccount(account);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // When/Then
        assertThrows(InvalidRequestException.class, () -> withdrawalStrategy.execute(transaction, productRepository));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testExecute_InvalidWithdrawalAmount() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));  // Positive amount

        // When/Then
        assertThrows(InvalidRequestException.class, () -> withdrawalStrategy.execute(transaction, productRepository));
        verify(productRepository, never()).save(any(Product.class));
    }
}
