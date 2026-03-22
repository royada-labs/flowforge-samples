package com.example.flowforge.market;

import com.example.flowforge.market.model.MarketIntelligenceReport;
import io.r2dbc.spi.ConnectionFactory;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@SpringBootApplication
public class MarketIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketIntelligenceApplication.class, args);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }

    @Bean
    public CommandLineRunner runner(FlowForgeClient client) {
        return args -> {
            String productId = args.length > 0 ? args[0] : "1";
            System.out.println("\n🌐 [Market Intelligence] Starting Complex Workflow for Product ID: " + productId);
            
            client.executeResult("market-intelligence", productId)
                    .map(result -> (MarketIntelligenceReport) result)
                    .doOnNext(report -> {
                        System.out.println("\n-------------------------------------------");
                        System.out.println("🌍 GLOBAL INTELLIGENCE REPORT COMPLETED 🌍");
                        System.out.println("-------------------------------------------");
                        System.out.println("📦 Product: " + report.getProduct().getTitle());
                        System.out.println("💰 Base Price: $" + report.getProduct().getPrice());
                        System.out.println("🏢 Stock: " + report.getStockLevel() + " units in " + report.getWarehouse());
                        System.out.println("📊 Pricing Verdict: " + report.getPriceVerdict());
                        System.out.println("🤖 Sentiment Trend: " + report.getOverallSentiment());
                        System.out.println("💹 Global Rates (USD): " + report.getCurrencyConversions());
                        System.out.println("⚠️ Risk Assessment: " + report.getRiskAssessment());
                        System.out.println("📝 Summary: " + report.getSummary());
                        System.out.println("🕒 Generated at: " + report.getCalculatedAt());
                        System.out.println("-------------------------------------------");
                    })
                    .onErrorComplete(e -> {
                        System.err.println("❌ ERROR IN WORKFLOW: " + e.getMessage());
                        return true;
                    })
                    .block();
        };
    }
}
