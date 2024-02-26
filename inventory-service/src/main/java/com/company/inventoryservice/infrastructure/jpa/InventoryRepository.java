package com.company.inventoryservice.infrastructure.jpa;

import com.company.inventoryservice.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i from Inventory i" +
            " WHERE i.skuCode IN :skuCodes")
    List<Inventory> findAllBySkuCode(List<String> skuCodes);

}
