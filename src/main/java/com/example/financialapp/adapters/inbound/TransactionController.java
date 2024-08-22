package com.example.financialapp.adapters.inbound;

import com.example.financialapp.application.service.TransactionService;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        logger.info("Fetching all transactions");
        List<Transaction> transactions = transactionService.findAll();
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        logger.info("Fetching transaction with id: {}", id);
        Transaction transaction = transactionService.getTransactionById(id)
                .orElseThrow(() -> {
                    logger.error("Transaction not found with id: {}", id);
                    return new ResourceNotFoundException("Transaction not found with id " + id);
                });
        logger.info("Transaction fetched successfully with id: {}", id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        logger.info("Creating new transaction");
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/source/{sourceAccountId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySourceAccountId(@PathVariable Long sourceAccountId) {
        logger.info("Fetching transactions for source account id: {}", sourceAccountId);
        List<Transaction> transactions = transactionService.getTransactionsBySourceAccountId(sourceAccountId);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for source account id: {}", sourceAccountId);
            return ResponseEntity.noContent().build();
        }
        logger.info("Number of transactions fetched for source account id {}: {}", sourceAccountId, transactions.size());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/destination/{destinationAccountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByDestinationAccountId(@PathVariable Long destinationAccountId) {
        logger.info("Fetching transactions for destination account id: {}", destinationAccountId);
        List<Transaction> transactions = transactionService.getTransactionsByDestinationAccountId(destinationAccountId);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for destination account id: {}", destinationAccountId);
            return ResponseEntity.noContent().build();
        }
        logger.info("Number of transactions fetched for destination account id {}: {}", destinationAccountId, transactions.size());
        return ResponseEntity.ok(transactions);
    }
}
