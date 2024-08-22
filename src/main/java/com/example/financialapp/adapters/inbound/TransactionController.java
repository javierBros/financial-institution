package com.example.financialapp.adapters.inbound;

import com.example.financialapp.application.service.TransactionService;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/source/{sourceAccountId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySourceAccountId(@PathVariable Long sourceAccountId) {
        List<Transaction> transactions = transactionService.getTransactionsBySourceAccountId(sourceAccountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/destination/{destinationAccountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByDestinationAccountId(@PathVariable Long destinationAccountId) {
        List<Transaction> transactions = transactionService.getTransactionsByDestinationAccountId(destinationAccountId);
        return ResponseEntity.ok(transactions);
    }
}
