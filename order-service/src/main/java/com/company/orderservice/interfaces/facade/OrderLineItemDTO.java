package com.company.orderservice.interfaces.facade;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDTO {

    @NotEmpty
    private String skuCode;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer quantity;
}
