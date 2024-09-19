package com.company.orderservice.configuration;

import com.company.orderservice.application.OrderService;
import com.company.orderservice.application.impl.OrderServiceImpl;
import com.company.orderservice.event.OrderPlacedEvent;
import com.company.orderservice.infrastructure.jpa.OrderRepository;
import com.company.orderservice.interfaces.facade.OrderFacade;
import com.company.orderservice.interfaces.facade.impl.OrderFacadeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OrderService orderService(OrderRepository orderRepository, ApplicationProperties applicationProperties, ReactorLoadBalancerExchangeFilterFunction loadBalancedFunction, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        return new OrderServiceImpl(orderRepository, applicationProperties, loadBalancedFunction, kafkaTemplate);
    }

    @Bean
    public OrderFacade orderFacade(OrderService orderService) {
        return new OrderFacadeImpl(orderService);
    }



}
