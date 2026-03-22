package com.example.flowforge.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBase {
    private Long id;
    private String title;
    private double price;
    private String category;
    private String description;
}
