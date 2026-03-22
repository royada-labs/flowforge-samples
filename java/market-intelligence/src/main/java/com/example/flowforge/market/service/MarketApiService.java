package com.example.flowforge.market.service;

import com.example.flowforge.market.model.ProductBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.time.Duration;

public class MarketApiService {

    private static final MarketApiService INSTANCE = new MarketApiService();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static MarketApiService getInstance() {
        return INSTANCE;
    }

    public Mono<ProductBase> fetchProductDetails(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://fakestoreapi.com/products/" + id))
                .GET()
                .build();
                
        return Mono.fromFuture(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .map(response -> {
                    try {
                        return mapper.readValue(response.body(), ProductBase.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing product from FakeStoreAPI", e);
                    }
                })
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<Double> fetchCompetitorPrice(String source, double basePrice) {
        // Simulating network delay and price variance
        return Mono.just(basePrice * (0.9 + Math.random() * 0.2))
                .delayElement(Duration.ofMillis(800 + (long)(Math.random() * 500)));
    }

    public Mono<Double> fetchSentiment(Long productId) {
        return Mono.just(10.0 + Math.random() * 80.0)
                .delayElement(Duration.ofMillis(600));
    }

    public Mono<Map<String, Double>> fetchExchanges() {
        // Pure Java fallback/mock
        return Mono.just(Map.of("EUR", 0.93, "JPY", 148.2, "MXN", 17.5))
                .delayElement(Duration.ofMillis(500));
    }
}
