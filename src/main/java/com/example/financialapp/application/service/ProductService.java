package com.example.financialapp.application.service;

import com.example.financialapp.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Product product, Long clientId);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
}
