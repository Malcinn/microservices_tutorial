package com.company.productservice.interfaces.facade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank(message = "Name is required.")
                             String name,
                             @NotBlank(message = "Description is required")
                             @Size(min = 5, max = 30, message = "Description must be from 3 to 30 characters length.")
                             String description,
                             @NotNull(message = "price is required.")
                             BigDecimal price) {
}
