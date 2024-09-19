package com.company.inventoryservice.interfaces.facade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryResponseDTO {
    private String skuCode;
    private boolean isInStock;
}
