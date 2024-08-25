package com.example.financialapp.application.service.impl;

import com.example.financialapp.adapters.outbound.ClientRepository;
import com.example.financialapp.domain.Client;
import com.example.financialapp.domain.Product;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateClient_Underage() {
        // Given
        Client client = new Client();
        client.setBirthdate(LocalDate.now().minusYears(17));

        // When/Then
        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () -> {
            clientService.createClient(client);
        });

        assertEquals(Constants.CLIENT_UNDERAGE, thrown.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testCreateClient_Success() {
        // Given
        Client client = new Client();
        client.setBirthdate(LocalDate.now().minusYears(20));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        Client savedClient = clientService.createClient(client);

        // Then
        assertNotNull(savedClient);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testUpdateClient_NotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.updateClient(1L, new Client());
        });

        assertEquals(Constants.CLIENT_NOT_FOUND + 1L, thrown.getMessage());
    }

    @Test
    void testUpdateClient_Success() {
        // Given
        Client existingClient = new Client();
        existingClient.setId(1L);
        Client clientDetails = new Client();
        clientDetails.setFirstName("Updated Name");
        clientDetails.setLastName("Updated LastName");
        clientDetails.setEmail("updated@example.com");

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        Client updatedClient = clientService.updateClient(1L, clientDetails);

        // Then
        assertNotNull(updatedClient);
        assertEquals("Updated Name", updatedClient.getFirstName());
        assertEquals("Updated LastName", updatedClient.getLastName());
        assertEquals("updated@example.com", updatedClient.getEmail());
        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.deleteClient(1L);
        });

        assertEquals(Constants.CLIENT_NOT_FOUND + 1L, thrown.getMessage());
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void testDeleteClient_HasProducts() {
        // Given
        Client client = new Client();
        client.setId(1L);
        client.setProducts(List.of(new Product()));

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // When/Then
        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () -> {
            clientService.deleteClient(1L);
        });

        assertEquals(Constants.CLIENT_HAS_PRODUCTS, thrown.getMessage());
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void testDeleteClient_Success() {
        // Given
        Client client = new Client();
        client.setId(1L);
        client.setProducts(new ArrayList<>());

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testGetAllClients() {
        // Given
        Client client1 = new Client();
        client1.setId(1L);
        Client client2 = new Client();
        client2.setId(2L);
        List<Client> clients = Arrays.asList(client1, client2);

        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<Client> result = clientService.getAllClients();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientById_Success() {
        // Given
        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // When
        Optional<Client> result = clientService.getClientById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Client> result = clientService.getClientById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(clientRepository, times(1)).findById(1L);
    }
}
