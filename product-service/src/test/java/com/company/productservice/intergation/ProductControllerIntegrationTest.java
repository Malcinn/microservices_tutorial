package com.company.productservice.intergation;

import com.company.productservice.domain.Product;
import com.company.productservice.infrastructure.jpa.ProductRepository;
import com.company.productservice.interfaces.facade.ProductRequest;
import com.company.productservice.interfaces.facade.ProductResponse;
import org.hamcrest.beans.HasProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductControllerIntegrationTest {

    private static final String PRODUCTS_API = "/api/product";

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void productController_createProduct_should_create_product() {
        ProductRequest productRequest = new ProductRequest("test-name", "test-description", new BigDecimal(123));

        webTestClient.post()
                .uri(PRODUCTS_API)
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(ProductResponse.class)
                .value(prodResponse -> new HasProperty<ProductResponse>("id"));

        List<Product> result = productRepository.findAll();
        Assertions.assertEquals(1, result.size());

    }

    @Test
    void productController_createProduct_error_when_invalid_data() {
        ProductRequest productRequest = new ProductRequest(null, null, null);

        webTestClient.post()
                .uri(PRODUCTS_API)
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void productController_getAllProducts_should_return_list_of_products() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "test-name", "test-description", new BigDecimal(123)));
        products.add(new Product("2", "test-name", "test-description", new BigDecimal(123)));
        products.add(new Product("3", "test-name", "test-description", new BigDecimal(123)));
        productRepository.insert(products);

        webTestClient.get()
                .uri(PRODUCTS_API)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBodyList(ProductResponse.class)
                .hasSize(4);

        List<Product> result = productRepository.findAll();
        Assertions.assertEquals(4, result.size());
    }
}
