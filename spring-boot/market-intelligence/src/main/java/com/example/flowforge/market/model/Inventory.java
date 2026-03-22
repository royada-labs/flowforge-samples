package com.example.flowforge.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("inventory")
public class Inventory {
    @Id
    private Long id;
    private Long productId;
    private int quantity;
    private String warehouseLocation;
}
