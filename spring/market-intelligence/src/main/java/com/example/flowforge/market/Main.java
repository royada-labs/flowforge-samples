package com.example.flowforge.market;

import com.example.flowforge.market.model.MarketIntelligenceReport;
import io.r2dbc.spi.ConnectionFactory;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Plain Spring] Starting Market Intelligence Analysis (No-Boot)");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // Blocking Schema Init
        initDbSchema(context);
        
        FlowForgeClient client = context.getBean(FlowForgeClient.class);
        String productId = args.length > 0 ? args[0] : "1";
        
        System.out.println("Processing ID: " + productId);
        
        try {
            client.executeResult("market-intelligence", productId)
                    .map(res -> (MarketIntelligenceReport) res)
                    .doOnNext(report -> {
                        System.out.println("\n-------------------------------------------");
                        System.out.println("🌍 GLOBAL INTELLIGENCE REPORT COMPLETED (Plain Spring) 🌍");
                        System.out.println("-------------------------------------------");
                        System.out.println("📦 Product: " + report.getProduct().getTitle());
                        System.out.println("💰 Base Price: $" + report.getProduct().getPrice());
                        System.out.println("🏢 Stock: " + report.getStockLevel() + " units in " + report.getWarehouse());
                        System.out.println("📊 Verdict: " + report.getPriceVerdict());
                        System.out.println("🤖 Sentiment: " + report.getOverallSentiment());
                        System.out.println("💹 Rates: " + report.getCurrencyConversions());
                        System.out.println("📝 Summary: " + report.getSummary());
                        System.out.println("-------------------------------------------\n");
                    })
                    .onErrorResume(e -> {
                        System.err.println("❌ WORKFLOW FAILED: " + e.getMessage());
                        e.printStackTrace();
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
        } finally {
            context.close();
        }
        
        System.out.println("--- Plain Spring Demo Finished ---");
        System.exit(0);
    }

    private static void initDbSchema(AnnotationConfigApplicationContext context) {
        System.out.println("🔨 Initializing Database Schema...");
        ConnectionFactory connectionFactory = context.getBean(ConnectionFactory.class);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        populator.populate(connectionFactory).block(); // ENSURE it's done
        System.out.println("✅ Schema initialized.");
    }
}
