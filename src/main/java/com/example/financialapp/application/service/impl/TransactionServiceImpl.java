package com.example.financialapp.application.service.impl;

import com.example.financialapp.application.service.TransactionService;
import com.example.financialapp.application.service.strategy.TransactionStrategy;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.domain.TransactionType;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.adapters.outbound.TransactionRepository;
import com.example.financialapp.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final Map<TransactionType, TransactionStrategy> strategyMap;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ProductRepository productRepository,
                                  Map<TransactionType, TransactionStrategy> strategyMap) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.strategyMap = strategyMap;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionType() == null) {
            throw new InvalidRequestException(Constants.INVALID_TRANSACTION);
        }

        TransactionStrategy strategy = strategyMap.get(transaction.getTransactionType());
        if (strategy == null) {
            throw new InvalidRequestException(Constants.INVALID_TRANSACTION_TYPE);
        }

        strategy.execute(transaction, productRepository);
        transactionRepository.save(transaction);
        return transaction;
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
