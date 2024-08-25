package com.example.financialapp.application.service.strategy;

import com.example.financialapp.application.service.strategy.TransactionStrategy;
import com.example.financialapp.domain.Product;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class TransferStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, ProductRepository productRepository) {

        Product sourceAccount = productRepository.findById(transaction.getSourceAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getSourceAccount().getId()));

        Product destinationAccount = productRepository.findById(transaction.getDestinationAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getDestinationAccount().getId()));

        if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InvalidRequestException(Constants.INSUFFICIENT_BALANCE);
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));

        productRepository.save(sourceAccount);
        productRepository.save(destinationAccount);

        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
    }
}
