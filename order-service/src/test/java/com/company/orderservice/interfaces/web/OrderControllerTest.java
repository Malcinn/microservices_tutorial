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
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    private static final String ORDER_API_URI = "/api/order";

    @MockBean
    OrderFacade orderFacade;

    @Autowired
    WebTestClient webTestClient;

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
