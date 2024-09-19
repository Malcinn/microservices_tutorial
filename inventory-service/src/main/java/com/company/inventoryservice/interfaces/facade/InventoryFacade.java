package com.company.inventoryservice.interfaces.facade;

import reactor.core.publisher.Flux;

import java.util.List;

public interface InventoryFacade {
    Flux<InventoryResponseDTO> findAllBySkuCode(List<String> skuCodes);
}
