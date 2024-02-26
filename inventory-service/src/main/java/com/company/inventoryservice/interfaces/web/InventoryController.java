package com.company.inventoryservice.interfaces.web;

import com.company.inventoryservice.interfaces.facade.InventoryFacade;
import com.company.inventoryservice.interfaces.facade.InventoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryFacade inventoryFacade;

    @GetMapping()
    public Flux<InventoryResponseDTO> areInStock(@RequestParam("skuCode") List<String> skuCodes) {
        return inventoryFacade.findAllBySkuCode(skuCodes);
    }
}
