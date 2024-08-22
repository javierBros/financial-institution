package com.example.financialapp.application.service;

import com.example.financialapp.domain.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getTransactionsBySourceAccountId(Long sourceAccountId);
    List<Transaction> getTransactionsByDestinationAccountId(Long destinationAccountId);
    Optional<Transaction> getTransactionById(Long id);
    List<Transaction> findAll();
}
