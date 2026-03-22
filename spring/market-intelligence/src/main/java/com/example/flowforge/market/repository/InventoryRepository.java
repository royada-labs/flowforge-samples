package com.example.flowforge.market.repository;

import com.example.flowforge.market.model.Inventory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Long> {

    @Query("SELECT * FROM inventory WHERE product_id = :productId LIMIT 1")
    Mono<Inventory> findByProductId(Long productId);
}
