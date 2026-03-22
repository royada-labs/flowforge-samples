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
    public Mono<Inventory> checkInventory(ProductBase product) {
        System.out.println("📦 [Task 3] Querying DB for Inventory...");
        return inventoryRepository.findByProductId(product.getId())
                .defaultIfEmpty(Inventory.builder()
                        .productId(product.getId())
                        .quantity(5)
                        .warehouseLocation("Mock Almacén")
                        .build());
    }

    @FlowTask(id = "fetchAmazon")
    public Mono<Double> fetchAmazon(Inventory inventory, ReactiveExecutionContext ctx) {
        System.out.println("📱 [Task 4] Branch A: Checking Amazon...");
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        return apiService.fetchCompetitorPrice("Amazon", product.getPrice());
    }

    @FlowTask(id = "fetchEbay")
    public Mono<Double> fetchEbay(Inventory inventory, ReactiveExecutionContext ctx) {
        System.out.println("📱 [Task 5] Branch A: Checking Ebay...");
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        return apiService.fetchCompetitorPrice("Ebay", product.getPrice());
    }

    @FlowTask(id = "calcCompetitiveIndex")
    public Mono<String> calcIndex(Object ignored, ReactiveExecutionContext ctx) {
        System.out.println("🧩 [Task 6] Calculating Pricing Index...");
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        double amazon = ctx.get(TaskDefinition.of(TaskId.of("fetchAmazon"), Inventory.class, Double.class).outputKey()).orElse(0.0);
        double ebay = ctx.get(TaskDefinition.of(TaskId.of("fetchEbay"), Inventory.class, Double.class).outputKey()).orElse(0.0);

        if (product.getPrice() < Math.min(amazon, ebay)) return Mono.just("HIGHLY_COMPETITIVE");
        return Mono.just("FAIR_MARKET_VALUE");
    }

    @FlowTask(id = "fetchSocial")
    public Mono<Double> fetchSocial(Inventory inventory) {
        System.out.println("📡 [Task 7] Branch B: Social Metrics...");
        return apiService.fetchSentimentScore(inventory.getProductId());
    }

    @FlowTask(id = "analyzeSentiment")
    public Mono<String> analyzeSentiment(Double score) {
        System.out.println("🤖 [Task 8] Sentiment AI...");
        return Mono.just(score > 60 ? "VERY_POSITIVE" : (score > 30 ? "NEUTRAL" : "NEGATIVE"));
    }

    @FlowTask(id = "fetchFinance")
    public Mono<Map<String, Double>> fetchFinance(Inventory inventory) {
        System.out.println("💰 [Task 9] Branch C: Currency Conversion...");
        return apiService.fetchExchanges();
    }

    @FlowTask(id = "assessRisk")
    public Mono<String> assessRisk(Object ignored, ReactiveExecutionContext ctx) {
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        Inventory inventory = ctx.get(TaskDefinition.of(TaskId.of("checkInventory"), ProductBase.class, Inventory.class).outputKey()).orElseThrow();
        
        if (product.getPrice() > 500 || inventory.getQuantity() < 10) {
            System.out.println("⚠️ [Task 10] HIGH RISK detected.");
            return Mono.just("HIGH_EXPOSURE_RISK");
        }
        return Mono.just("STABLE");
    }

    @FlowTask(id = "consolidateReport")
    public Mono<MarketIntelligenceReport> consolidate(Object ignored, ReactiveExecutionContext ctx) {
        System.out.println("📜 [Task 11] Building final Master Report...");
        
        ProductBase product = ctx.get(TaskDefinition.of(TaskId.of("fetchBaseDetails"), Long.class, ProductBase.class).outputKey()).orElseThrow();
        Inventory inv = ctx.get(TaskDefinition.of(TaskId.of("checkInventory"), ProductBase.class, Inventory.class).outputKey()).orElseThrow();
        
        double amz = ctx.get(TaskDefinition.of(TaskId.of("fetchAmazon"), Inventory.class, Double.class).outputKey()).orElse(0.0);
        double eb = ctx.get(TaskDefinition.of(TaskId.of("fetchEbay"), Inventory.class, Double.class).outputKey()).orElse(0.0);
        
        String index = ctx.get(TaskDefinition.of(TaskId.of("calcCompetitiveIndex"), Object.class, String.class).outputKey()).orElse("N/A");
        String sentiment = ctx.get(TaskDefinition.of(TaskId.of("analyzeSentiment"), Double.class, String.class).outputKey()).orElse("N/A");
        String risk = ctx.get(TaskDefinition.of(TaskId.of("assessRisk"), Object.class, String.class).outputKey()).orElse("NONE");
        
        Map<String, Double> rates = (Map<String, Double>) ctx.get(TaskDefinition.of(TaskId.of("fetchFinance"), Inventory.class, Map.class).outputKey()).orElse(Map.of());

        return Mono.just(MarketIntelligenceReport.builder()
                .product(product)
                .stockLevel(inv.getQuantity())
                .warehouse(inv.getWarehouseLocation())
                .amazonPrice(amz)
                .ebayPrice(eb)
                .priceVerdict(index)
                .overallSentiment(sentiment)
                .riskAssessment(risk)
                .currencyConversions(rates)
                .summary(String.format("Analyze complete. Product is %s with %s sentiment.", index, sentiment))
                .calculatedAt(LocalDateTime.now().toString())
                .build());
    }

    @FlowTask(id = "finalOutput")
    public Mono<MarketIntelligenceReport> finalizeReport(MarketIntelligenceReport report) {
        System.out.println("✅ [Task 12] Final step.");
        return Mono.just(report);
    }
}
