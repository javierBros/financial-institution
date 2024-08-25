package com.example.financialapp.application.service.strategy;

import com.example.financialapp.domain.Transaction;
import com.example.financialapp.adapters.outbound.ProductRepository;

public interface TransactionStrategy {
    void execute(Transaction transaction, ProductRepository productRepository);
}
