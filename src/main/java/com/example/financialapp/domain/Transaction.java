package com.example.financialapp.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = true)
    private Product sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = true)
    private Product destinationAccount;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}
