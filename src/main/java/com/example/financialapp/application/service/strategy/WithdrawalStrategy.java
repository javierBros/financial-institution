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
public class WithdrawalStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, ProductRepository productRepository) {
        validateWithdrawalAmount(transaction.getAmount());

        Product account = productRepository.findById(transaction.getSourceAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + transaction.getSourceAccount().getId()));

        if (account.getBalance().compareTo(transaction.getAmount().abs()) < 0) {
            throw new InvalidRequestException(Constants.INSUFFICIENT_BALANCE);
        }

        account.setBalance(account.getBalance().subtract(transaction.getAmount().abs()));
        productRepository.save(account);

        transaction.setSourceAccount(account);
    }

    private void validateWithdrawalAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidRequestException("The withdrawal amount must be negative.");
        }
    }
}
