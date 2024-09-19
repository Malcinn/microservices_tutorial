package com.company.orderservice.interfaces.web;

import com.company.orderservice.interfaces.facade.OrderFacade;
import com.company.orderservice.interfaces.facade.OrderRequestDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderFacade orderFacade;

    @PostMapping
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public Mono<ResponseEntity<String>> placeOrder(@Validated @RequestBody OrderRequestDTO orderRequest) {
        return orderFacade.placeOrder(orderRequest)
                .map(orderNumber -> new ResponseEntity<>(MessageFormat.format("Order placed successfully, Order number: {0}", orderNumber), HttpStatus.CREATED));
        // In case of usage of circuit breaker we do not need to handle errors in that way
        //.onErrorReturn(new ResponseEntity<>("Order placement failed", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> exceptionHandler(MethodArgumentNotValidException exception, HttpRequest request) {
        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", exception.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    public Mono<ResponseEntity<String>> inventoryFallback(OrderRequestDTO orderRequestDTO, Throwable throwable) {
        return Mono.just(new ResponseEntity<>("Ops.. Inventory service unavailable, can not proceed further, please order after some time. Error " + throwable.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
