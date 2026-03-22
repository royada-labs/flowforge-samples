package com.example.flowforge.ecommerce;

import com.example.flowforge.ecommerce.service.OrderService;
import org.royada.flowforge.spring.autoconfig.FlowForgeAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.example.flowforge.ecommerce")
@Import(FlowForgeAutoConfiguration.class) // Manually import the FlowForge configuration
public class EcommerceAppConfig {

    public static void main(String[] args) {
        // In plain Spring, we manually create and refresh the context
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EcommerceAppConfig.class)) {
            System.out.println("--- Starting Plain Spring FlowForge Demo ---");
            
            OrderService orderService = context.getBean(OrderService.class);
            
            // Execute the workflow and wait for the result
            orderService.processOrder("order-plain-spring-123")
                    .doOnSuccess(v -> System.out.println("Workflow execution completed successfully!"))
                    .doOnError(e -> System.err.println("Workflow execution failed: " + e.getMessage()))
                    .block();
            
            System.out.println("--- Plain Spring Demo Finished ---");
        }
    }
}
