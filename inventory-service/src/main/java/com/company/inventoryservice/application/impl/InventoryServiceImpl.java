package com.company.inventoryservice.application.impl;

import com.company.inventoryservice.application.InventoryService;
import com.company.inventoryservice.domain.Inventory;
import com.company.inventoryservice.infrastructure.jpa.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


    @Override
    public List<Inventory> findAllBySkuCode(List<String> skuCodes) {
        return inventoryRepository.findAllBySkuCode(skuCodes);
    }
}
