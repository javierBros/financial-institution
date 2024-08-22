package com.example.financialapp.application.service;

import com.example.financialapp.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client createClient(Client client);
    Client updateClient(Long id, Client clientDetails);
    void deleteClient(Long id);
    List<Client> getAllClients();
    Optional<Client> getClientById(Long id);
}
