package com.example.flowforge.market.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MarketIntelligenceReport {
    private ProductBase product;
    private int stockLevel;
    private String warehouse;
    
    // Competitive results
    private double amazonPrice;
    private double ebayPrice;
    private String priceVerdict; // Competitive, Expensive, Cheap
    
    // Social results
    private String overallSentiment; // POSITIVE, NEGATIVE, NEUTRAL
    private double sentimentScore;
    
    // Financial
    private Map<String, Double> currencyConversions;
    
    // Optional
    private String riskAssessment; // CRITICAL, STABLE, ACTION_REQUIRED
    
    private String summary;
    private String calculatedAt;
}
