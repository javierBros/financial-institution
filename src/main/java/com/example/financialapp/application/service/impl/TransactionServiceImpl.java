package com.example.financialapp.application.service.impl;

import com.example.financialapp.application.service.TransactionService;
import com.example.financialapp.domain.Product;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.domain.TransactionType;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.adapters.outbound.TransactionRepository;
import com.example.financialapp.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        // Example of handling different types of transactions
        if (transaction.getTransactionType() == null) {
            throw new InvalidRequestException(Constants.INVALID_TRANSACTION);
        }

        // Additional logic for handling transfers, deposits, withdrawals, etc.
        if (transaction.getTransactionType() == TransactionType.TRANSFER) {
            Product sourceAccount = productRepository.findById(transaction.getSourceAccount().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getSourceAccount().getId()));

            Product destinationAccount = productRepository.findById(transaction.getDestinationAccount().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getDestinationAccount().getId()));

            // Validate sufficient balance
            if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InvalidRequestException(Constants.INSUFFICIENT_BALANCE);
            }

            // Deduct from source and add to destination
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));

            productRepository.save(sourceAccount);
            productRepository.save(destinationAccount);
        }

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsBySourceAccountId(Long sourceAccountId) {
        return transactionRepository.findBySourceAccountId(sourceAccountId);
    }

    @Override
    public List<Transaction> getTransactionsByDestinationAccountId(Long destinationAccountId) {
        return transactionRepository.findByDestinationAccountId(destinationAccountId);
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
}
