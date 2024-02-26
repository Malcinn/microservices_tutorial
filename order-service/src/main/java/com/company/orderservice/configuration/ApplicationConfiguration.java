package com.company.orderservice.configuration;

import com.company.orderservice.application.OrderService;
import com.company.orderservice.application.impl.OrderServiceImpl;
import com.company.orderservice.infrastructure.jpa.OrderRepository;
import com.company.orderservice.interfaces.facade.OrderFacade;
import com.company.orderservice.interfaces.facade.impl.OrderFacadeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OrderService orderService(OrderRepository orderRepository, ApplicationProperties applicationProperties, ReactorLoadBalancerExchangeFilterFunction loadBalancedFunction) {
        return new OrderServiceImpl(orderRepository, applicationProperties, loadBalancedFunction);
    }

    @Bean
    public OrderFacade orderFacade(OrderService orderService) {
        return new OrderFacadeImpl(orderService);
    }


}
