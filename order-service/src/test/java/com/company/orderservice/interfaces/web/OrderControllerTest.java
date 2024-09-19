package com.company.orderservice.interfaces.web;

import com.company.orderservice.interfaces.facade.OrderFacade;
import com.company.orderservice.interfaces.facade.OrderLineItemDTO;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    private static final String ORDER_API_URI = "/api/order";

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0-debian"));

    @MockBean
    OrderFacade orderFacade;

    @Autowired
    WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Test
    void OrderController_placeOrder_order_should_be_placed_successfully() {
        Collection<OrderLineItemDTO> orderLineItemDTOS = new ArrayList<>();
        orderLineItemDTOS.add(new OrderLineItemDTO("123", BigDecimal.valueOf(123L), 123));
        orderLineItemDTOS.add(new OrderLineItemDTO("234", BigDecimal.valueOf(234L), 234));
        orderLineItemDTOS.add(new OrderLineItemDTO("345", BigDecimal.valueOf(345L), 345));
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderLineItemDTOS(orderLineItemDTOS);
        Mockito.when(orderFacade.placeOrder(Mockito.any(OrderRequestDTO.class))).thenReturn(Mono.just("123"));
        webTestClient.post().uri(ORDER_API_URI)
                .bodyValue(orderRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);
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
