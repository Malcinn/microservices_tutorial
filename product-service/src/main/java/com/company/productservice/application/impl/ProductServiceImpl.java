package com.company.productservice.application.impl;

import com.company.productservice.application.ProductService;
import com.company.productservice.domain.Product;
import com.company.productservice.infrastructure.jpa.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(String name, String description, BigDecimal price) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
        return productRepository.insert(product);

    }

    @Override
    public Collection<Product> getAll() {
        return productRepository.findAll();
    }
}
