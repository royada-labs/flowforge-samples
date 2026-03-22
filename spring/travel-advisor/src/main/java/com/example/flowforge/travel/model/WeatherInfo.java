package com.example.flowforge.travel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherInfo {
    private double currentTemp;
    private String conditions;
    private double windSpeed;
}
