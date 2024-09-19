package com.company.orderservice.application.impl;

import com.company.orderservice.application.InventoryResponse;
import com.company.orderservice.application.OrderService;
import com.company.orderservice.configuration.ApplicationProperties;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderLineItem;
import com.company.orderservice.event.OrderPlacedEvent;
import com.company.orderservice.infrastructure.jpa.OrderRepository;
import com.company.orderservice.interfaces.facade.OrderLineItemDTO;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String INVENTORY_PATH = "/api/inventory";

    private OrderRepository orderRepository;

    private ApplicationProperties applicationProperties;

    private final ReactorLoadBalancerExchangeFilterFunction loadBalancedFunction;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Transactional
    @Override
    public Mono<String> placeOrder(OrderRequestDTO orderRequest) {
        return getProductInventory(CollectionUtils.emptyIfNull(orderRequest.getOrderLineItemDTOS()).stream()
                .map(OrderLineItemDTO::getSkuCode)
                .toList())
                .filter(InventoryResponse::isInStock)
                .map(InventoryResponse::getSkuCode)
                .collectList()
//  In case of usage of circuit breaker we do not need to handle errors in that way
//                .onErrorResume(throwable -> {
//                    LOGGER.error("Service unavailable, exception message:{}", throwable.getMessage());
//                    throwable.printStackTrace();
//                    return Mono.just(Collections.emptyList());
//                })
                .map(inStockInventories -> {
                    Order order = this.map(orderRequest, inStockInventories);
                    orderRepository.save(order);
                    kafkaTemplate.send(applicationProperties.getTopicName(), new OrderPlacedEvent(order.getOrderNumber()));
                    return order.getOrderNumber();
                });
    }

    private String nextOrderNumber() {
        return UUID.randomUUID().toString();
    }

    private Order map(OrderRequestDTO orderRequestDTO, List<String> inStockProducts) {
        Order order = new Order();
        order.setOrderLineItems(orderRequestDTO.getOrderLineItemDTOS().stream()
                .map(orderLineItemDTO -> map(orderLineItemDTO, inStockProducts))
                .filter(Objects::nonNull)
                .toList());
        order.setOrderNumber(nextOrderNumber());
        return order;
    }

    private OrderLineItem map(OrderLineItemDTO orderLineItemDTO, List<String> inStockProducts) {
        if (isProductInStock(orderLineItemDTO.getSkuCode(), inStockProducts)) {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setSkuCode(orderLineItemDTO.getSkuCode());
            orderLineItem.setPrice(orderLineItemDTO.getPrice());
            orderLineItem.setQuantity(orderLineItemDTO.getQuantity());
            return orderLineItem;
        }
        return null;
    }

    private boolean isProductInStock(String skuCode, List<String> inStockProducts) {
        return inStockProducts.contains(skuCode);
    }

    private Flux<InventoryResponse> getProductInventory(List<String> skuCodes) {
        String skuCodesParam = skuCodes.stream()
                .map(s -> "skuCode=" + s)
                .collect(Collectors.joining("&"));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                .filter(loadBalancedFunction)
                .baseUrl(applicationProperties.getInventoryServiceBaseURL())
                .build()
                .get()
                .uri(String.format("%s?%s", INVENTORY_PATH, skuCodesParam))
                .retrieve()
                .bodyToFlux(InventoryResponse.class);
    }
}

