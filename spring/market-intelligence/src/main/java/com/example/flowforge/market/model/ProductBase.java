package com.example.flowforge.market.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductBase {
    private Long id;
    private String title;
    private double price;
    private String category;
    private String description;
}
