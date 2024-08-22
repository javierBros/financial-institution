package com.example.financialapp.adapters.inbound;

import com.example.financialapp.application.service.ClientService;
import com.example.financialapp.domain.Client;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private Logger logger;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClients() {
        // Given
        Client client1 = new Client();
        client1.setId(1L);
        Client client2 = new Client();
        client2.setId(2L);
        List<Client> clients = Arrays.asList(client1, client2);

        when(clientService.getAllClients()).thenReturn(clients);

        // When
        ResponseEntity<List<Client>> response = clientController.getAllClients();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(clientService, times(1)).getAllClients();
    }

    @Test
    void testGetClientById() {
        // Given
        Client client = new Client();
        client.setId(1L);
        when(clientService.getClientById(anyLong())).thenReturn(Optional.of(client));

        // When
        ResponseEntity<Client> response = clientController.getClientById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(clientService, times(1)).getClientById(1L);
    }

    @Test
    void testGetClientByIdNotFound() {
        // Given
        when(clientService.getClientById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        ResourceNotFoundException thrown = 
            org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                clientController.getClientById(1L);
            });

        assertEquals("Client not found with id 1", thrown.getMessage());
    }

    @Test
    void testCreateClient() {
        // Given
        Client client = new Client();
        client.setId(1L);
        when(clientService.createClient(any(Client.class))).thenReturn(client);

        // When
        ResponseEntity<Client> response = clientController.createClient(client);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(clientService, times(1)).createClient(client);
    }

    @Test
    void testUpdateClient() {
        // Given
        Client client = new Client();
        client.setId(1L);
        when(clientService.updateClient(anyLong(), any(Client.class))).thenReturn(client);

        // When
        ResponseEntity<Client> response = clientController.updateClient(1L, client);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(clientService, times(1)).updateClient(1L, client);
    }

    @Test
    void testDeleteClient() {
        // When
        ResponseEntity<Void> response = clientController.deleteClient(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clientService, times(1)).deleteClient(1L);
    }
}
