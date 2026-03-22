package com.example.flowforge.travel;

import com.example.flowforge.travel.model.TravelReport;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Plain Spring] Starting Travel Advisor...");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            FlowForgeClient client = context.getBean(FlowForgeClient.class);
            
            String country = args.length > 0 ? args[0] : "Spain";
            
            System.out.println("--- Travel Advisor Workflow Processing: " + country + " ---");

            client.executeResult("travel-advisor", country)
                    .map(result -> (TravelReport) result)
                    .doOnNext(report -> {
                        System.out.println("\n✅ Travel Report Ready (from Plain Spring):");
                        System.out.println("📍 Country: " + report.getCountry());
                        System.out.println("🏙️ Capital: " + report.getCapital());
                        System.out.println("💰 Currency: " + report.getCurrency());
                        System.out.println("🌡️ Current Temp: " + report.getTemperature() + "°C");
                        System.out.println("☁️ Conditions: " + report.getSkyCondition());
                    })
                    .doOnError(e -> System.err.println("❌ Workflow failed: " + e.getMessage()))
                    .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                    .block();
        }
        
        System.out.println("--- Plain Spring Demo Finished ---");
    }
}
