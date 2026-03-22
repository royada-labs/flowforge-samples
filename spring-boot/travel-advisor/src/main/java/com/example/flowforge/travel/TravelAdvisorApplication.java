package com.example.flowforge.travel;

import com.example.flowforge.travel.model.TravelReport;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TravelAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAdvisorApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(FlowForgeClient client) {
        return args -> {
            String country = args.length > 0 ? args[0] : "Spain";
            
            System.out.println("--- Travel Advisor Workflow Execution ---");
            System.out.println("Processing report for: " + country);

            client.executeResult("travel-advisor", country)
                    .map(result -> (TravelReport) result)
                    .subscribe(
                            report -> {
                                System.out.println("\n✅ Travel Report Ready:");
                                System.out.println("📍 Country: " + report.getCountry());
                                System.out.println("🏙️ Capital: " + report.getCapital());
                                System.out.println("💰 Currency: " + report.getCurrency());
                                System.out.println("🌡️ Current Temp: " + report.getTemperature() + "°C");
                                System.out.println("☁️ Conditions: " + report.getSkyCondition());
                            },
                            error -> System.err.println("❌ Workflow failed: " + error.getMessage())
                    );
        };
    }
}
