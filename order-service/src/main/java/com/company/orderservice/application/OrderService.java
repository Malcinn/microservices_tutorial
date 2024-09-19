package com.company.orderservice.application;

import com.company.orderservice.domain.Order;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface OrderService {

    Mono<String> placeOrder(OrderRequestDTO orderRequest);
}
