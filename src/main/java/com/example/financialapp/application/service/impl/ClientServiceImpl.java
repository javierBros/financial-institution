package com.example.financialapp.application.service.impl;

import com.example.financialapp.application.service.ClientService;
import com.example.financialapp.domain.Client;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.infrastructure.repository.ClientRepository;
import com.example.financialapp.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client createClient(Client client) {
        if (client.getBirthdate().plusYears(18).isAfter(LocalDateTime.now())) {
            throw new InvalidRequestException(Constants.CLIENT_UNDERAGE);
        }
        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CLIENT_NOT_FOUND + id));

        client.setFirstName(clientDetails.getFirstName());
        client.setLastName(clientDetails.getLastName());
        client.setEmail(clientDetails.getEmail());
        client.setUpdatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CLIENT_NOT_FOUND + id));

        if (!client.getProducts().isEmpty()) {
            throw new InvalidRequestException(Constants.CLIENT_HAS_PRODUCTS);
        }
        clientRepository.delete(client);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }
}
