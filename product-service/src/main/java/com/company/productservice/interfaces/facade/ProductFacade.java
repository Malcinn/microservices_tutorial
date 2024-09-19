package com.company.productservice.interfaces.facade;

import java.util.Collection;

public interface ProductFacade {
    ProductResponse createProduct(ProductRequest productDTO);

    public Collection<ProductResponse> getAllProducts();
}
