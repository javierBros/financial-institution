package com.example.financialapp.application.service.impl;

import com.example.financialapp.adapters.outbound.ClientRepository;
import com.example.financialapp.application.service.ProductService;
import com.example.financialapp.domain.AccountStatus;
import com.example.financialapp.domain.AccountType;
import com.example.financialapp.domain.Client;
import com.example.financialapp.domain.Product;
import com.example.financialapp.infrastructure.exception.InvalidRequestException;
import com.example.financialapp.infrastructure.exception.ResourceNotFoundException;
import com.example.financialapp.adapters.outbound.ProductRepository;
import com.example.financialapp.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ClientRepository clientRepository) {
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Product createProduct(Product product, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId));

        validateAndSetDefaults(product);

        if (product.getStatus() == AccountStatus.CANCELED) {
            throw new InvalidRequestException("Cannot create a product with a CANCELLED status");
        }
        product.setClient(client);
        Product savedProduct = productRepository.save(product);
        savedProduct.setAccountNumber(generateAccountNumber(savedProduct));

        return productRepository.save(savedProduct);
    }

    private void validateAndSetDefaults(Product product) {
        if (product.getAccountType() == AccountType.SAVINGS) {
            // Ensure savings account balance is not negative
            if (product.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidRequestException("Savings account balance cannot be negative");
            }
            // Set default status to ACTIVE for savings accounts
            if (product.getStatus() == null) {
                product.setStatus(AccountStatus.ACTIVE);
            }
        }
    }

    private String generateAccountNumber(Product product) {
        String prefix = product.getAccountType() == AccountType.SAVINGS ? "53" : "33";
        String uniqueNumber = String.format("%08d", product.getId()); // Use the generated ID of the product
        return prefix + uniqueNumber;
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + id));

        if (productDetails.getStatus() == AccountStatus.CANCELED && product.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidRequestException("Cannot cancel an account with a non-zero balance");
        }
        product.setStatus(productDetails.getStatus());
        product.setBalance(productDetails.getBalance());
        product.setGmfExempt(productDetails.isGmfExempt());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PRODUCT_NOT_FOUND + id));
        if (product.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidRequestException("Cannot delete an account with a non-zero balance");
        }

        productRepository.delete(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}
