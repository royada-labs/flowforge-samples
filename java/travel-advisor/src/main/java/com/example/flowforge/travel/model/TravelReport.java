package com.example.flowforge.travel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelReport {
    private String country;
    private String capital;
    private String currency;
    private double temperature;
    private String skyCondition;
}
