package com.example.flowforge.ecommerce.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private String id;
    private Double amount;
    private String status;
    private String customerEmail;
}
