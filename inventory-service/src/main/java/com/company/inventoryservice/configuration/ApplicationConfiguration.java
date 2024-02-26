package com.company.inventoryservice.configuration;

import com.company.inventoryservice.application.InventoryService;
import com.company.inventoryservice.application.impl.InventoryServiceImpl;
import com.company.inventoryservice.domain.Inventory;
import com.company.inventoryservice.infrastructure.jpa.InventoryRepository;
import com.company.inventoryservice.interfaces.facade.InventoryFacade;
import com.company.inventoryservice.interfaces.facade.impl.InventoryFacadeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Bean
    public InventoryService inventoryService(InventoryRepository inventoryRepository) {
        return new InventoryServiceImpl(inventoryRepository);
    }

    @Bean
    public InventoryFacade inventoryFacade(InventoryService inventoryService) {
        return new InventoryFacadeImpl(inventoryService);
    }

    @Bean
    public CommandLineRunner loadSampleData(InventoryRepository inventoryRepository){
        LOGGER.info("Loading Sample Data...");
        return (args -> inventoryRepository.saveAll(List.of(
                new Inventory(1L,"portal_gun",2),
                new Inventory(2L,"portal_fluid",100),
                new Inventory(3L,"space_cruiser",0)
        )));
    }
}
