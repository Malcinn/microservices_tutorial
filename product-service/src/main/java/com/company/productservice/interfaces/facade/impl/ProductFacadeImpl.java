package com.company.productservice.interfaces.facade.impl;

import com.company.productservice.application.ProductService;
import com.company.productservice.domain.Product;
import com.company.productservice.interfaces.facade.ProductFacade;
import com.company.productservice.interfaces.facade.ProductRequest;
import com.company.productservice.interfaces.facade.ProductResponse;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.stream.Collectors;

public class ProductFacadeImpl implements ProductFacade {

    private final ProductService productService;

    public ProductFacadeImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productDTO) {
        Product product = productService.create(productDTO.name(), productDTO.description(), productDTO.price());
        return map(product);
    }

    @Override
    public Collection<ProductResponse> getAllProducts() {
        Collection<Product> products = productService.getAll();
        return products.stream().map(this::map).toList();
    }

    private ProductResponse map(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
