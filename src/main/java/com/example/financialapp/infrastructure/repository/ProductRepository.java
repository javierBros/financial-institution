package com.example.financialapp.infrastructure.repository;

import com.example.financialapp.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByClientId(Long clientId);
    Optional<Product> findByAccountNumber(String accountNumber);
}
