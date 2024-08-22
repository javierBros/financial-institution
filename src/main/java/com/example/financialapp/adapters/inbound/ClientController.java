package com.example.financialapp.adapters.inbound;

import com.example.financialapp.application.service.ClientService;
import com.example.financialapp.domain.Client;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Validated
public class ClientController {

    private final ClientService clientService;
    private final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        logger.info("Fetching all clients");
        List<Client> clients = clientService.getAllClients();
        logger.debug("Number of clients fetched: {}", clients.size());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        logger.info("Fetching client with id: {}", id);
        Client client = clientService.getClientById(id)
                .orElseThrow(() -> {
                    logger.warn("Client not found with id: {}", id);
                    return new ResourceNotFoundException("Client not found with id " + id);
                });
        logger.info("Client found: {}", client);
        return ResponseEntity.ok(client);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        logger.info("Creating new client: {}", client);
        Client createdClient = clientService.createClient(client);
        logger.info("Client created successfully with id: {}", createdClient.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @Valid @RequestBody Client clientDetails) {
        logger.info("Updating client with id: {}", id);
        Client updatedClient = clientService.updateClient(id, clientDetails);
        logger.info("Client updated successfully with id: {}", updatedClient.getId());
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        logger.info("Deleting client with id: {}", id);
        clientService.deleteClient(id);
        logger.info("Client with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
