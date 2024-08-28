package com.example.financialapp.application.service.impl;

import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.adapters.outbound.TransactionRepository;
import com.example.financialapp.application.service.impl.TransactionServiceImpl;
import com.example.financialapp.application.service.strategy.TransactionStrategy;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.domain.TransactionType;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionStrategy depositStrategy;

    @Mock
    private TransactionStrategy withdrawalStrategy;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking strategy map
        Map<TransactionType, TransactionStrategy> strategyMap = new HashMap<>();
        strategyMap.put(TransactionType.DEPOSIT, depositStrategy);
        strategyMap.put(TransactionType.WITHDRAWAL, withdrawalStrategy);

        transactionService = new TransactionServiceImpl(transactionRepository, productRepository, strategyMap);
    }

    @Test
    void testCreateTransaction_Deposit() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);

        // When
        transactionService.createTransaction(transaction);

        // Then
        verify(depositStrategy, times(1)).execute(any(Transaction.class), eq(productRepository));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_UnknownType() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER); // Assume no strategy is mapped for TRANSFER

        // When/Then
        assertThrows(InvalidRequestException.class, () -> transactionService.createTransaction(transaction));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionById() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));

        // When
        Optional<Transaction> result = transactionService.getTransactionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionsBySourceAccountId() {
        // When
        transactionService.getTransactionsBySourceAccountId(1L);

        // Then
        verify(transactionRepository, times(1)).findBySourceAccountId(1L);
    }

    @Test
    void testGetTransactionsByDestinationAccountId() {
        // When
        transactionService.getTransactionsByDestinationAccountId(1L);

        // Then
        verify(transactionRepository, times(1)).findByDestinationAccountId(1L);
    }
}
