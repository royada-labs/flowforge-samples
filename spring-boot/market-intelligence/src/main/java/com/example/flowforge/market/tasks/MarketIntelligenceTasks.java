package com.example.flowforge.market.tasks;

import com.example.flowforge.market.model.*;
import com.example.flowforge.market.repository.InventoryRepository;
import com.example.flowforge.market.service.MarketApiService;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.royada.flowforge.task.TaskDefinition;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.time.LocalDateTime;

@Component
@TaskHandler("market-intel")
public class MarketIntelligenceTasks {

    private final MarketApiService apiService;
    private final InventoryRepository inventoryRepository;

    public MarketIntelligenceTasks(MarketApiService apiService, InventoryRepository inventoryRepository) {
        this.apiService = apiService;
        this.inventoryRepository = inventoryRepository;
    }

    @FlowTask(id = "initInquiry")
    public Mono<Long> init(String productIdStr) {
        System.out.println("🚀 [Task 1] Initializing Market Inquiry for: " + productIdStr);
        return Mono.just(Long.valueOf(productIdStr));
    }

    @FlowTask(id = "fetchBaseDetails")
    public Mono<ProductBase> fetchBase(Long id) {
        System.out.println("🎬 [Task 2] Fetching Base Info for Product ID: " + id);
        return apiService.fetchProductDetails(id);
    }

    @FlowTask(id = "checkInventory")
    public Mono<ProductBase> checkInventory(ProductBase product) {
        System.out.println("📦 [Task 3] Checking Inventory in Reactive DB...");
        return inventoryRepository.findByProductId(product.getId())
                .defaultIfEmpty(Inventory.builder().productId(product.getId()).quantity(5).warehouseLocation("Mock Almacén").build())
                .thenReturn(product);
    }

    @FlowTask(id = "fetchAmazon")
    public Mono<Double> fetchAmazon(ProductBase product) {
        System.out.println("📱 [Task 4] Parallel Branch A: Checking Amazon Price...");
        return apiService.fetchCompetitorPrice("Amazon", product.getPrice());
    }

    @FlowTask(id = "fetchEbay")
    public Mono<Double> fetchEbay(ProductBase product) {
        System.out.println("📱 [Task 5] Parallel Branch A: Checking Ebay Price...");
        return apiService.fetchCompetitorPrice("Ebay", product.getPrice());
    }

    @FlowTask(id = "calcCompetitiveIndex")
    public Mono<String> calcIndex(Object ignored, ReactiveExecutionContext ctx) {
        System.out.println("🧩 [Task 6] Joining Branch A results...");
        
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        double amazon = ctx.get(TaskDefinition.of(TaskId.of("fetchAmazon"), ProductBase.class, Double.class).outputKey()).orElse(0.0);
        double ebay = ctx.get(TaskDefinition.of(TaskId.of("fetchEbay"), ProductBase.class, Double.class).outputKey()).orElse(0.0);

        if (product.getPrice() < Math.min(amazon, ebay)) return Mono.just("HIGHLY_COMPETITIVE");
        return Mono.just("FAIR_MARKET");
    }

    @FlowTask(id = "fetchSocial")
    public Mono<Double> fetchSocial(ProductBase product) {
        System.out.println("📡 [Task 7] Parallel Branch B: Fetching Social Metrics...");
        return apiService.fetchSentimentScore(product.getId());
    }

    @FlowTask(id = "analyzeSentiment")
    public Mono<String> analyzeSentiment(Double score) {
        System.out.println("🤖 [Task 8] Analyzing Sentiment for Score: " + score);
        return Mono.just(score > 50 ? "POSITIVE" : "NEUTRAL");
    }

    @FlowTask(id = "fetchFinance")
    public Mono<Map<String, Double>> fetchFinance(Object ignored) {
        System.out.println("💰 [Task 9] Parallel Branch C: Fetching Real Exchange Rates...");
        return apiService.fetchExchanges();
    }

    @FlowTask(id = "assessRisk")
    public Mono<String> assessRisk(Object ignored, ReactiveExecutionContext ctx) {
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        
        if (product.getPrice() < 500) {
            System.out.println("⚠️ [Task 10] Skipping Risk Assessment (Price too low: $" + product.getPrice() + ")");
            return Mono.just("SKIPPED: Low Value");
        }
        
        System.out.println("⚠️ [Task 10] Evaluating High-Value Risk Analysis...");
        return Mono.just("STABLE: High Value Verified");
    }

    @FlowTask(id = "consolidateReport")
    public Mono<MarketIntelligenceReport> consolidate(Object ignored, ReactiveExecutionContext ctx) {
        System.out.println("📜 [Task 11] Summarizing all intelligence points...");
        
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        String index = ctx.get(TaskDefinition.of(TaskId.of("calcCompetitiveIndex"), Object.class, String.class).outputKey()).orElse("N/A");
        String sentiment = ctx.get(TaskDefinition.of(TaskId.of("analyzeSentiment"), Double.class, String.class).outputKey()).orElse("N/A");
        String risk = ctx.get(TaskDefinition.of(TaskId.of("assessRisk"), Object.class, String.class).outputKey()).orElse("NOT_ASSESSED");

        return Mono.just(MarketIntelligenceReport.builder()
                .product(product)
                .stockLevel(15) 
                .priceVerdict(index)
                .overallSentiment(sentiment)
                .riskAssessment(risk)
                .calculatedAt(LocalDateTime.now().toString())
                .build());
    }

    @FlowTask(id = "finalOutput")
    public Mono<MarketIntelligenceReport> finalizeReport(MarketIntelligenceReport report) {
        System.out.println("✅ [Task 12] Final report ready.");
        return Mono.just(report);
    }
}
