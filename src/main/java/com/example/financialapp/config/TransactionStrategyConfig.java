package com.example.financialapp.config;

import com.example.financialapp.application.service.strategy.DepositStrategy;
import com.example.financialapp.application.service.strategy.TransactionStrategy;
import com.example.financialapp.application.service.strategy.TransferStrategy;
import com.example.financialapp.application.service.strategy.WithdrawalStrategy;
import com.example.financialapp.domain.TransactionType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class TransactionStrategyConfig {

    @Bean
    public Map<TransactionType, TransactionStrategy> strategyMap(
            DepositStrategy depositStrategy,
            WithdrawalStrategy withdrawalStrategy,
            TransferStrategy transferStrategy) {
        Map<TransactionType, TransactionStrategy> strategyMap = new EnumMap<>(TransactionType.class);
        strategyMap.put(TransactionType.DEPOSIT, depositStrategy);
        strategyMap.put(TransactionType.WITHDRAWAL, withdrawalStrategy);
        strategyMap.put(TransactionType.TRANSFER, transferStrategy);
        return strategyMap;
    }
}
