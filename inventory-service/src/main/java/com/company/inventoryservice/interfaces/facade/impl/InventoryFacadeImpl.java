package com.company.inventoryservice.interfaces.facade.impl;

import com.company.inventoryservice.application.InventoryService;
import com.company.inventoryservice.domain.Inventory;
import com.company.inventoryservice.interfaces.facade.InventoryFacade;
import com.company.inventoryservice.interfaces.facade.InventoryResponseDTO;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
public class InventoryFacadeImpl implements InventoryFacade {

    private InventoryService inventoryService;

    @Override
    public Flux<InventoryResponseDTO> findAllBySkuCode(List<String> skuCodes) {
        return Flux.fromIterable(inventoryService.findAllBySkuCode(skuCodes)
                .stream().map(this::map)
                .toList());
    }

    public InventoryResponseDTO map(Inventory inventory) {
        return new InventoryResponseDTO(inventory.getSkuCode(), inventory.isInStock());
    }
}
