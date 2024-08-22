package com.example.financialapp.infrastructure.repository;

import com.example.financialapp.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByIdentificationNumber(String identificationNumber);
    Optional<Client> findByEmail(String email);
}
