package com.example.financialapp.application.service.impl.strategy;

import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.application.service.strategy.DepositStrategy;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepositStrategyTest {

    @Mock
    private ProductRepository productRepository;

    private DepositStrategy depositStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        depositStrategy = new DepositStrategy();
    }

    @Test
    void testExecute_Success() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        Product account = new Product();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));

        transaction.setDestinationAccount(account);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // When
        depositStrategy.execute(transaction, productRepository);

        // Then
        verify(productRepository, times(1)).save(any(Product.class));
    }

}

