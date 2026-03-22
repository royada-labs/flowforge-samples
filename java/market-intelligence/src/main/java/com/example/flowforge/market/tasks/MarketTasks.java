package com.example.flowforge.market.tasks;

import com.example.flowforge.market.model.ProductBase;
import com.example.flowforge.market.model.Inventory;
import com.example.flowforge.market.model.MarketIntelligenceReport;
import com.example.flowforge.market.service.MarketApiService;
import io.r2dbc.spi.ConnectionFactory;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.FlowKey;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.time.LocalDateTime;

public class MarketTasks {

    // PRE-DEFINED KEYS for easy access across tasks
    // Static instances used for safe FlowKey extraction across tasks
    private static final FetchBaseTask TASK_BASE = new FetchBaseTask();
    private static final CheckInventoryTask TASK_INV = new CheckInventoryTask(null); // Just for the key
    private static final FetchAmazonTask TASK_AMZ = new FetchAmazonTask();
    private static final FetchEbayTask TASK_EBAY = new FetchEbayTask();
    private static final CalcIndexTask TASK_INDEX = new CalcIndexTask();
    private static final SentimentTask TASK_SENTIMENT = new SentimentTask();
    private static final AssessRiskTask TASK_RISK = new AssessRiskTask();

    // 1. INIT
    public static class InitInquiryTask extends BasicTask<String, Long> {
        public InitInquiryTask() { super(TaskId.of("initInquiry"), String.class, Long.class); }
        @Override
        public Mono<Long> doExecute(String input, ReactiveExecutionContext ctx) {
            System.out.println("🚀 [Pure Java] Task 1: Init inquiry for " + input);
            return Mono.just(Long.valueOf(input));
        }
    }

    // 2. FETCH BASE
    public static class FetchBaseTask extends BasicTask<Long, ProductBase> {
        public FetchBaseTask() { super(TaskId.of("fetchBase"), Long.class, ProductBase.class); }
        @Override
        public Mono<ProductBase> doExecute(Long id, ReactiveExecutionContext ctx) {
            System.out.println("🎬 Task 2: Fetching from FakeStore API...");
            return MarketApiService.getInstance().fetchProductDetails(id);
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("initInquiry")); }
    }

    // 3. CHECK INVENTORY
    public static class CheckInventoryTask extends BasicTask<ProductBase, Inventory> {
        private final ConnectionFactory factory;
        public CheckInventoryTask(ConnectionFactory factory) {
            super(TaskId.of("checkInventory"), ProductBase.class, Inventory.class);
            this.factory = factory;
        }
        @Override
        public Mono<Inventory> doExecute(ProductBase product, ReactiveExecutionContext ctx) {
            System.out.println("📦 Task 3: Raw R2DBC SQL query...");
            return Mono.from(factory.create())
                    .flatMap(conn -> Mono.from(conn.createStatement("SELECT * FROM inventory WHERE product_id = $1")
                            .bind("$1", product.getId())
                            .execute())
                            .flatMap(res -> Mono.from(res.map((row, meta) -> 
                                Inventory.builder()
                                    .productId(product.getId())
                                    .quantity(row.get("quantity", Integer.class))
                                    .warehouseLocation(row.get("warehouse_location", String.class))
                                    .build()))
                            )
                            .switchIfEmpty(Mono.just(Inventory.builder().productId(product.getId()).quantity(0).warehouseLocation("OUT_OF_STOCK").build()))
                            .doFinally(signal -> Mono.from(conn.close()).subscribe())
                    );
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("fetchBase")); }
    }

    // Parallel Branch A
    public static class FetchAmazonTask extends BasicTask<Inventory, Double> {
        public FetchAmazonTask() { super(TaskId.of("fetchAmazon"), Inventory.class, Double.class); }
        @Override
        public Mono<Double> doExecute(Inventory in, ReactiveExecutionContext ctx) {
            ProductBase p = ctx.get(TASK_BASE.outputKey()).orElseThrow();
            System.out.println("📱 Task 4: Checking Amazon price...");
            return MarketApiService.getInstance().fetchCompetitorPrice("Amazon", p.getPrice());
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("checkInventory")); }
    }

    public static class FetchEbayTask extends BasicTask<Inventory, Double> {
        public FetchEbayTask() { super(TaskId.of("fetchEbay"), Inventory.class, Double.class); }
        @Override
        public Mono<Double> doExecute(Inventory in, ReactiveExecutionContext ctx) {
            ProductBase p = ctx.get(TASK_BASE.outputKey()).orElseThrow();
            System.out.println("📱 Task 5: Checking Ebay price...");
            return MarketApiService.getInstance().fetchCompetitorPrice("Ebay", p.getPrice());
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("checkInventory")); }
    }

    public static class CalcIndexTask extends BasicTask<Object, String> {
        public CalcIndexTask() { super(TaskId.of("calcIndex"), Object.class, String.class); }
        @Override
        public Mono<String> doExecute(Object ignored, ReactiveExecutionContext ctx) {
            System.out.println("🧩 Task 6: Joining Amazon & Ebay results...");
            double amz = ctx.get(TASK_AMZ.outputKey()).orElse(0.0);
            double ebay = ctx.get(TASK_EBAY.outputKey()).orElse(0.0);
            return Mono.just(amz < ebay ? "AMAZON_WINS" : "EBAY_WINS");
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("fetchAmazon"), TaskId.of("fetchEbay")); }
    }

    // Branch B
    public static class FetchSocialTask extends BasicTask<Inventory, Double> {
        public FetchSocialTask() { super(TaskId.of("fetchSocial"), Inventory.class, Double.class); }
        @Override
        public Mono<Double> doExecute(Inventory in, ReactiveExecutionContext ctx) {
            System.out.println("📡 Task 7: Fetching Social metrics...");
            return MarketApiService.getInstance().fetchSentiment(in.getProductId());
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("checkInventory")); }
    }

    public static class SentimentTask extends BasicTask<Double, String> {
        public SentimentTask() { super(TaskId.of("analyzeSentiment"), Double.class, String.class); }
        @Override
        public Mono<String> doExecute(Double score, ReactiveExecutionContext ctx) {
            System.out.println("🤖 Task 8: Sentiment score analysis...");
            return Mono.just(score > 40 ? "POSITIVE" : "NEGATIVE");
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("fetchSocial")); }
    }

    // Branch C
    public static class FetchFinanceTask extends BasicTask<Inventory, Map> {
        public FetchFinanceTask() { super(TaskId.of("fetchFinance"), Inventory.class, Map.class); }
        @Override
        public Mono<Map> doExecute(Inventory in, ReactiveExecutionContext ctx) {
            System.out.println("💰 Task 9: Fetching Global Rates...");
            return MarketApiService.getInstance().fetchExchanges().map(m -> (Map)m);
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("checkInventory")); }
    }

    // Risk
    public static class AssessRiskTask extends BasicTask<Object, String> {
        public AssessRiskTask() { super(TaskId.of("assessRisk"), Object.class, String.class); }
        @Override
        public Mono<String> doExecute(Object ignored, ReactiveExecutionContext ctx) {
            ProductBase p = ctx.get(TASK_BASE.outputKey()).orElseThrow();
            System.out.println("⚠️ Task 10: Evaluating Risk...");
            if (p.getPrice() > 500) return Mono.just("HIGH_FINANCIAL_EXPOSURE");
            return Mono.just("STABLE_MARKET_CAP");
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("calcIndex"), TaskId.of("analyzeSentiment"), TaskId.of("fetchFinance")); }
    }

    // 11. CONSOLIDATE
    public static class ConsolidateTask extends BasicTask<Object, MarketIntelligenceReport> {
        public ConsolidateTask() { super(TaskId.of("consolidate"), Object.class, MarketIntelligenceReport.class); }
        @Override
        public Mono<MarketIntelligenceReport> doExecute(Object in, ReactiveExecutionContext ctx) {
            System.out.println("📜 Task 11: Building the Master Report...");
            ProductBase p = ctx.get(TASK_BASE.outputKey()).orElseThrow();
            Inventory inv = ctx.get(TASK_INV.outputKey()).orElseThrow();
            String resA = ctx.get(TASK_INDEX.outputKey()).orElse("N/A");
            String resB = ctx.get(TASK_SENTIMENT.outputKey()).orElse("N/A");
            String resRisk = ctx.get(TASK_RISK.outputKey()).orElse("N/A");

            return Mono.just(MarketIntelligenceReport.builder()
                    .product(p)
                    .stockLevel(inv.getQuantity())
                    .warehouse(inv.getWarehouseLocation())
                    .priceVerdict(resA)
                    .overallSentiment(resB)
                    .riskAssessment(resRisk)
                    .summary(String.format("Full Analysis Complete. Product is %s with %s sentiment.", resA, resB))
                    .calculatedAt(LocalDateTime.now().toString())
                    .build());
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("assessRisk")); }
    }

    // 12. FINAL
    public static class FinalTask extends BasicTask<MarketIntelligenceReport, MarketIntelligenceReport> {
        public FinalTask() { super(TaskId.of("finalOutput"), MarketIntelligenceReport.class, MarketIntelligenceReport.class); }
        @Override
        public Mono<MarketIntelligenceReport> doExecute(MarketIntelligenceReport report, ReactiveExecutionContext ctx) {
            System.out.println("✅ Task 12: Final result ready.");
            return Mono.just(report);
        }
        @Override public Set<TaskId> dependencies() { return Set.of(TaskId.of("consolidate")); }
    }
}
