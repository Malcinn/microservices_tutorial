package com.company.productservice.application;

import com.company.productservice.domain.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@Service
public interface ProductService {
    Product create(String name, String description, BigDecimal price);

    Collection<Product> getAll();
}
