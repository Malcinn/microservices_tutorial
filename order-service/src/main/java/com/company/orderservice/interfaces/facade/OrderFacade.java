package com.company.orderservice.interfaces.facade;

import reactor.core.publisher.Mono;

public interface OrderFacade {
    Mono<String> placeOrder(OrderRequestDTO orderRequest);
}
