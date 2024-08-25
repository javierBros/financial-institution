package com.example.financialapp.application.service.strategy;

import com.example.financialapp.application.service.strategy.TransactionStrategy;
import com.example.financialapp.domain.Product;
import com.example.financialapp.domain.Transaction;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.util.Constants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, ProductRepository productRepository) {
        validateDepositAmount(transaction.getAmount());

        Product account = productRepository.findById(transaction.getDestinationAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getDestinationAccount().getId()));

        account.setBalance(account.getBalance().add(transaction.getAmount()));
        productRepository.save(account);

        transaction.setDestinationAccount(account);
    }

    private void validateDepositAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("The deposit amount must be positive.");
        }
    }
}
