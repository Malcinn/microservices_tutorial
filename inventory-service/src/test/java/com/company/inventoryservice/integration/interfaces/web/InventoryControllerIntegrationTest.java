package com.company.inventoryservice.integration.interfaces.web;

import com.company.inventoryservice.domain.Inventory;
import com.company.inventoryservice.infrastructure.jpa.InventoryRepository;
import com.company.inventoryservice.interfaces.facade.InventoryResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerIntegrationTest {

    private static final String INVENTORY_API_URI = "/api/inventory";

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0-debian"));

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    InventoryRepository inventoryRepository;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }


    @Test
    void inventoryController_areInStock_should_return_collection_of_inStock_products() {
        List<String> skuCodes = List.of("1", "2", "3", "4", "5");
        inventoryRepository.saveAll(List.of(
                new Inventory(null, "1", 10),
                new Inventory(null, "2", 10),
                new Inventory(null, "3", 10),
                new Inventory(null, "4", 10),
                new Inventory(null, "5", 0)
        ));

        String productsParam = skuCodes.stream()
                .map(s -> "skuCode=" + s)
                .collect(Collectors.joining("&"));
        webTestClient.get().uri(INVENTORY_API_URI + "?" + productsParam)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBodyList(InventoryResponseDTO.class)
                .consumeWith(listEntityExchangeResult -> {
                    Assertions.assertNotNull(listEntityExchangeResult.getResponseBody());
                    Assertions.assertEquals(5, listEntityExchangeResult.getResponseBody().size());
                });
    }

    @Test
    void inventoryController_areInStock_should_return_empty_list_if_there_are_no_results() {
        List<String> skuCodes = List.of("6", "7", "8", "9", "10");

        String productsParam = skuCodes.stream()
                .map(s -> "skuCode=" + s)
                .collect(Collectors.joining("&"));
        webTestClient.get().uri(INVENTORY_API_URI + "?" + productsParam)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBodyList(InventoryResponseDTO.class)
                .consumeWith(listEntityExchangeResult -> {
                    Assertions.assertNotNull(listEntityExchangeResult.getResponseBody());
                    Assertions.assertEquals(0, listEntityExchangeResult.getResponseBody().size());
                });
    }

}
