package com.company.productservice.interfaces;

import com.company.productservice.interfaces.facade.ProductFacade;
import com.company.productservice.interfaces.facade.ProductRequest;
import com.company.productservice.interfaces.facade.ProductResponse;
import org.hamcrest.beans.HasProperty;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    private static final String PRODUCTS_API = "/api/product";

    @MockBean
    private ProductFacade productFacade;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void productController_createProduct_should_create_prodcut() {
        ProductResponse productResponse = new ProductResponse("1", "test-name", "test-description", new BigDecimal(123));
        Mockito.when(productFacade.createProduct(Mockito.any(ProductRequest.class))).thenReturn(productResponse);
        ProductRequest productRequest = new ProductRequest("test-name", "test-description", new BigDecimal(123));

        webTestClient.post()
                .uri(PRODUCTS_API)
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(ProductResponse.class)
                .value(prodResponse -> new HasProperty<ProductResponse>("id"));
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
        List<ProductResponse> products = new ArrayList<>();
        products.add(new ProductResponse("1", "test-name", "test-description", new BigDecimal(123)));
        products.add(new ProductResponse("2", "test-name", "test-description", new BigDecimal(123)));
        products.add(new ProductResponse("3", "test-name", "test-description", new BigDecimal(123)));

        Mockito.when(productFacade.getAllProducts()).thenReturn(products);
        webTestClient.get()
                .uri(PRODUCTS_API)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBodyList(ProductResponse.class)
                .hasSize(3);

    }

}
