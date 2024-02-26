package com.company.orderservice.interfaces.facade.impl;

import com.company.orderservice.application.OrderService;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderLineItem;
import com.company.orderservice.interfaces.facade.OrderFacade;
import com.company.orderservice.interfaces.facade.OrderLineItemDTO;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class OrderFacadeImpl implements OrderFacade {

    @Autowired
    private OrderService orderService;

    public OrderFacadeImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Mono<String> placeOrder(OrderRequestDTO orderRequest) {
        return orderService.placeOrder(orderRequest);
    }


}
