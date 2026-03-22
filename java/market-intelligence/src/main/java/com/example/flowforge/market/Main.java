package com.example.flowforge.market;

import com.example.flowforge.market.model.MarketIntelligenceReport;
import com.example.flowforge.market.tasks.MarketTasks.*;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.royada.flowforge.task.TaskDescriptor;
import org.royada.flowforge.workflow.input.DefaultTaskInputResolver;
import org.royada.flowforge.workflow.monitor.AsyncLoggingWorkflowMonitor;
import org.royada.flowforge.workflow.orchestrator.ReactiveWorkflowOrchestrator;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.royada.flowforge.workflow.plan.WorkflowPlanBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.io.InputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Pure Java] Complex Market Intelligence Analysis");

        // 1. Setup R2DBC H2
        ConnectionFactory connectionFactory = new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .inMemory("marketdb-java")
                        .username("sa")
                        .password("")
                        .property("DB_CLOSE_DELAY", "-1")
                        .build()
        );

        // 2. Manual Schema Init
        initDb(connectionFactory);

        // 3. Setup Tasks
        FinalTask finalTask = new FinalTask();

        // 4. Build Plan (12 Tasks)
        WorkflowExecutionPlan plan = WorkflowPlanBuilder.buildFromDescriptors(
                List.of(
                        new TaskDescriptor(new InitInquiryTask()),
                        new TaskDescriptor(new FetchBaseTask()),
                        new TaskDescriptor(new CheckInventoryTask(connectionFactory)),
                        new TaskDescriptor(new FetchAmazonTask()),
                        new TaskDescriptor(new FetchEbayTask()),
                        new TaskDescriptor(new CalcIndexTask()),
                        new TaskDescriptor(new FetchSocialTask()),
                        new TaskDescriptor(new SentimentTask()),
                        new TaskDescriptor(new FetchFinanceTask()),
                        new TaskDescriptor(new AssessRiskTask()),
                        new TaskDescriptor(new ConsolidateTask()),
                        new TaskDescriptor(finalTask)
                ),
                java.util.Collections.emptyMap()
        );

        // 5. Setup Orchestrator
        ReactiveWorkflowOrchestrator orchestrator = ReactiveWorkflowOrchestrator.builder()
                .monitor(new AsyncLoggingWorkflowMonitor())
                .inputResolver(new DefaultTaskInputResolver())
                .taskScheduler(Schedulers.parallel())
                .build();

        // 6. Execute
        String productId = args.length > 0 ? args[0] : "1";
        System.out.println("Processing ID: " + productId);

        orchestrator.execute("market-intelligence-java", plan, productId)
                .map(ctx -> (MarketIntelligenceReport) ctx.get(finalTask.outputKey()).orElseThrow())
                .doOnNext(report -> {
                    System.out.println("\n-------------------------------------------");
                    System.out.println("🌍 GLOBAL INTELLIGENCE REPORT COMPLETED (Pure Java) 🌍");
                    System.out.println("-------------------------------------------");
                    System.out.println("📦 Product: " + report.getProduct().getTitle());
                    System.out.println("💰 Base Price: $" + report.getProduct().getPrice());
                    System.out.println("🏢 Stock & Loc: " + report.getStockLevel() + " @ " + report.getWarehouse());
                    System.out.println("📊 Pricing Result: " + report.getPriceVerdict());
                    System.out.println("🤖 Sentiment Trend: " + report.getOverallSentiment());
                    System.out.println("⚠️ Risk Diagnosis: " + report.getRiskAssessment());
                    System.out.println("📝 Summary: " + report.getSummary());
                    System.out.println("🕒 Generated: " + report.getCalculatedAt());
                    System.out.println("-------------------------------------------\n");
                })
                .doOnError(e -> {
                    System.err.println("❌ WORKFLOW FAILED: " + e.getMessage());
                    e.printStackTrace();
                })
                .onErrorResume(e -> Mono.empty())
                .block();

        System.out.println("--- Pure Java Demo Finished ---");
        System.exit(0);
    }

    private static void initDb(ConnectionFactory factory) {
        System.out.println("🔨 Initializing H2 Schema (Raw R2DBC)...");
        try {
            InputStream is = Main.class.getResourceAsStream("/schema.sql");
            if (is == null) throw new RuntimeException("schema.sql not found!");
            String schema = new Scanner(is).useDelimiter("\\A").next();
            
            Mono.from(factory.create())
                .flatMapMany(conn -> {
                    String[] statements = schema.split(";");
                    return reactor.core.publisher.Flux.fromArray(statements)
                            .filter(s -> !s.trim().isEmpty())
                            .flatMap(s -> Mono.from(conn.createStatement(s).execute()))
                            .concatWith(Mono.from(conn.close()).then(Mono.empty()));
                })
                .blockLast();
            System.out.println("✅ Database schema initialized.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }
}
