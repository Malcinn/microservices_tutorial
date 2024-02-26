package com.company.inventoryservice.application;

import com.company.inventoryservice.domain.Inventory;

import java.util.List;

public interface InventoryService {

    List<Inventory> findAllBySkuCode(List<String> skuCodes);
}
