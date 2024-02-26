package com.company.inventoryservice.interfaces.web;

import com.company.inventoryservice.interfaces.facade.InventoryFacade;
import com.company.inventoryservice.interfaces.facade.InventoryResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerTest {

    private static final String INVENTORY_API_URI = "/api/inventory";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    InventoryFacade inventoryFacade;

    @Test
    void inventoryController_areInStock_should_return_collection_of_inStock_products() {
        List<String> skuCodes = List.of("1", "2", "3", "4", "5");
        List<InventoryResponseDTO> inventoryResponseDTOList = List.of(
                new InventoryResponseDTO("1", true),
                new InventoryResponseDTO("2", true),
                new InventoryResponseDTO("3", true),
                new InventoryResponseDTO("4", true),
                new InventoryResponseDTO("5", false)
        );
        Mockito.when(inventoryFacade.findAllBySkuCode(skuCodes)).thenReturn(Flux.fromIterable(inventoryResponseDTOList));

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
        Mockito.when(inventoryFacade.findAllBySkuCode(skuCodes)).thenReturn(Flux.empty());

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
