package com.example.flowforge.market.service;

import com.example.flowforge.market.model.ProductBase;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Service
public class MarketApiService {

    private final WebClient webClient = WebClient.create();
    private final Random random = new Random();

    public Mono<ProductBase> fetchProductDetails(Long productId) {
        // Real API: FakeStoreAPI
        return webClient.get()
                .uri("https://fakestoreapi.com/products/" + productId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> ProductBase.builder()
                        .id(productId)
                        .title(node.path("title").asText("Product " + productId))
                        .price(node.path("price").asDouble(0.0))
                        .category(node.path("category").asText("N/A"))
                        .description(node.path("description").asText("N/A"))
                        .build())
                .delayElement(Duration.ofMillis(500)); // Simulate extra latency
    }

    public Mono<Double> fetchCompetitorPrice(String source, double basePrice) {
        // Mock API call with delay
        return Mono.just(basePrice * (0.85 + (1.15 - 0.85) * random.nextDouble()))
                .delayElement(Duration.ofMillis(1200));
    }

    public Mono<Double> fetchSentimentScore(Long productId) {
        // Mock API call with delay
        return Mono.just(random.nextDouble() * 100.0)
                .delayElement(Duration.ofMillis(1000));
    }

    public Mono<Map<String, Double>> fetchExchanges() {
        return webClient.get()
                .uri("https://v6.exchangerate-api.com/v6/latest/USD") // Public if valid, otherwise fallback
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    JsonNode rates = node.path("conversion_rates");
                    return Map.of(
                            "EUR", rates.path("EUR").asDouble(0.92),
                            "JPY", rates.path("JPY").asDouble(150.0),
                            "BRL", rates.path("BRL").asDouble(5.0)
                    );
                })
                .onErrorResume(e -> Mono.just(Map.of("EUR", 0.93, "JPY", 148.0, "BRL", 4.95)))
                .delayElement(Duration.ofMillis(800));
    }
}
