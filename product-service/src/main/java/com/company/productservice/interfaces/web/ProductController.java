package com.company.productservice.interfaces.web;

import com.company.productservice.interfaces.facade.ProductFacade;
import com.company.productservice.interfaces.facade.ProductRequest;
import com.company.productservice.interfaces.facade.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductFacade productFacade;

    @PostMapping
    public ResponseEntity<Mono<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productDTO) {

        return new ResponseEntity<>(Mono.just(productFacade.createProduct(productDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public Flux<ProductResponse> getAllProducts() {
        return Flux.fromIterable(productFacade.getAllProducts());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> notValid(MethodArgumentNotValidException ex, HttpRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
