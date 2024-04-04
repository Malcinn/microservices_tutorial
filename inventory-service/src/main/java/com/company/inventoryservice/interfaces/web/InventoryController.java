package com.company.inventoryservice.interfaces.web;

import com.company.inventoryservice.interfaces.facade.InventoryFacade;
import com.company.inventoryservice.interfaces.facade.InventoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryFacade inventoryFacade;

    @GetMapping()
    public Flux<InventoryResponseDTO> areInStock(@RequestParam("skuCode") List<String> skuCodes) throws InterruptedException {
//        Long sleepDuration = 5000L;
//        if (Objects.nonNull(sleepDuration)){
//            Thread.sleep(sleepDuration);
//        }
        return inventoryFacade.findAllBySkuCode(skuCodes);
    }
}
