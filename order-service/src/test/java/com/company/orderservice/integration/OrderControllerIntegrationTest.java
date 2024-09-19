package com.company.orderservice.integration;

import com.company.orderservice.application.InventoryResponse;
import com.company.orderservice.configuration.ApplicationTestConfiguration;
import com.company.orderservice.configuration.TestConsumer;
import com.company.orderservice.domain.Order;
import com.company.orderservice.infrastructure.jpa.OrderRepository;
import com.company.orderservice.interfaces.facade.OrderLineItemDTO;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Import(ApplicationTestConfiguration.class)
class OrderControllerIntegrationTest {
    private static final String ORDER_API_URI = "/api/order";

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0-debian"));

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    TestConsumer testConsumer;

    static MockWebServer mockWebServer;


    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("order.service.inventoryServiceBaseURL", () -> mockWebServer.url("").toString());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.kafka.bootstraps-servers", kafka::getBootstrapServers);
    }

    @Test
    void OrderController_placeOrder_order_should_be_placed_successfully() throws IOException, InterruptedException {
        List<InventoryResponse> inventoryResponse = List.of(
                new InventoryResponse("1", true),
                new InventoryResponse("2", true),
                new InventoryResponse("3", false)
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(inventoryResponse))
                .setHeader("Content-type", "application/json")
                .setResponseCode(200)
        );

        Collection<OrderLineItemDTO> orderLineItemDTOS = new ArrayList<>();
        orderLineItemDTOS.add(new OrderLineItemDTO("1", BigDecimal.valueOf(12L), 1));
        orderLineItemDTOS.add(new OrderLineItemDTO("2", BigDecimal.valueOf(12L), 1));
        orderLineItemDTOS.add(new OrderLineItemDTO("3", BigDecimal.valueOf(12L), 1));
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderLineItemDTOS(orderLineItemDTOS);

        webTestClient.post().uri(ORDER_API_URI)
                .bodyValue(orderRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody().consumeWith(entityExchangeResult -> {
                    Assertions.assertNotNull(entityExchangeResult.getResponseBody());
                });

        List<Order> result = orderRepository.findAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertNotNull(result.get(0).getOrderNumber());
        Assertions.assertNotNull(result.get(0).getId());
        Assertions.assertEquals(2, result.get(0).getOrderLineItems().size());

        boolean messageConsumed = testConsumer.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
        Assertions.assertEquals(result.get(0).getOrderNumber(),testConsumer.getPayload());
    }

    @Test
    void OrderController_placeOrder_order_should_not_be_placed_in_case_of_invalid_data() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        webTestClient.post().uri(ORDER_API_URI)
                .bodyValue(orderRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
