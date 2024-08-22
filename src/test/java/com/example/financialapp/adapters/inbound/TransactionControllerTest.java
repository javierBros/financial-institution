package com.example.financialapp.adapters.inbound;

import com.example.financialapp.application.service.TransactionService;
import com.example.financialapp.domain.Transaction;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTransactions() {
        // Given
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionService.findAll()).thenReturn(transactions);

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(transactionService, times(1)).findAll();
    }

    @Test
    void testGetAllTransactionsNoContent() {
        // Given
        when(transactionService.findAll()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).findAll();
    }

    @Test
    void testGetTransactionById() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.of(transaction));

        // When
        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    void testGetTransactionByIdNotFound() {
        // Given
        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        ResourceNotFoundException thrown =
            org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                transactionController.getTransactionById(1L);
            });

        assertEquals("Transaction not found with id 1", thrown.getMessage());
        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    void testCreateTransaction() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);

        // When
        ResponseEntity<Transaction> response = transactionController.createTransaction(transaction);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(transactionService, times(1)).createTransaction(transaction);
    }

    @Test
    void testCreateTransactionError() {
        // Given
        when(transactionService.createTransaction(any(Transaction.class))).thenThrow(new RuntimeException("Error creating transaction"));

        // When
        ResponseEntity<Transaction> response = transactionController.createTransaction(new Transaction());

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(transactionService, times(1)).createTransaction(any(Transaction.class));
    }

    @Test
    void testGetTransactionsBySourceAccountId() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        List<Transaction> transactions = Arrays.asList(transaction);

        when(transactionService.getTransactionsBySourceAccountId(anyLong())).thenReturn(transactions);

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsBySourceAccountId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(transactionService, times(1)).getTransactionsBySourceAccountId(1L);
    }

    @Test
    void testGetTransactionsBySourceAccountIdNoContent() {
        // Given
        when(transactionService.getTransactionsBySourceAccountId(anyLong())).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsBySourceAccountId(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).getTransactionsBySourceAccountId(1L);
    }

    @Test
    void testGetTransactionsByDestinationAccountId() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        List<Transaction> transactions = Arrays.asList(transaction);

        when(transactionService.getTransactionsByDestinationAccountId(anyLong())).thenReturn(transactions);

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByDestinationAccountId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(transactionService, times(1)).getTransactionsByDestinationAccountId(1L);
    }

    @Test
    void testGetTransactionsByDestinationAccountIdNoContent() {
        // Given
        when(transactionService.getTransactionsByDestinationAccountId(anyLong())).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByDestinationAccountId(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).getTransactionsByDestinationAccountId(1L);
    }
}
