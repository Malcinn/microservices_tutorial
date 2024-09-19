package com.company.orderservice.interfaces.facade;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    @NotNull
    private Collection<OrderLineItemDTO> orderLineItemDTOS;
}
