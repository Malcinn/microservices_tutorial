package com.company.productservice.configuration;

import com.company.productservice.application.ProductService;
import com.company.productservice.application.impl.ProductServiceImpl;
import com.company.productservice.domain.Product;
import com.company.productservice.infrastructure.jpa.ProductRepository;
import com.company.productservice.interfaces.facade.ProductFacade;
import com.company.productservice.interfaces.facade.impl.ProductFacadeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    ProductRepository productRepository;

    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductServiceImpl(productRepository);
    }

    @Bean
    public ProductFacade productFacade(ProductService productService) {
        return new ProductFacadeImpl(productService);
    }

    @Bean
    public CommandLineRunner loadSampleData(ProductRepository productRepository){
        LOGGER.info("Loading Sample Data...");
        return (args -> productRepository.saveAll(List.of(
                new Product("portal_gun","Portal gun","Portal gun", BigDecimal.valueOf(799,1)),
                new Product("portal_fluid","Portal gun","Portal gun", new BigDecimal(100)),
                new Product("space_cruiser","Portal gun","Portal gun", new BigDecimal(99999))
        )));
    }
}
