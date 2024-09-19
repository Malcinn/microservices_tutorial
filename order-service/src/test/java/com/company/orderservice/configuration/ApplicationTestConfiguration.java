package com.company.orderservice.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@TestConfiguration
public class ApplicationTestConfiguration {

    @Bean
    public ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction(
            ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory,
            ObjectProvider<List<LoadBalancerClientRequestTransformer>> transformers) {
        return new ReactorLoadBalancerExchangeFilterFunction(loadBalancerFactory,
                transformers.getIfAvailable(Collections::emptyList)){
            @Override
            public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction next) {
                return next.exchange(clientRequest);
            }
        };
    }

    @Bean
    public TestConsumer testConsumer(){
        return new TestConsumer();
    }

}
