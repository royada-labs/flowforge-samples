package com.example.flowforge.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String warehouseLocation;
}
