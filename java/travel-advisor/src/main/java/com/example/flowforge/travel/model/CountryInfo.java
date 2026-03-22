package com.example.flowforge.travel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryInfo {
    private String name;
    private String capital;
    private double latitude;
    private double longitude;
    private String currencyCode;
    private String currencySymbol;
}
