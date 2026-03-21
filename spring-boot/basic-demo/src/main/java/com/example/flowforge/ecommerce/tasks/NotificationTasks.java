package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@TaskHandler( "notifications")
public class NotificationTasks {

    @FlowTask(id = "notifyResult", retryMaxRetries = 3)
    public Mono<Void> notifyResult(ValidationResult result) {
        // Lab Scenario: Randomly fail with a network error
        if (Math.random() < 0.7) {
            System.err.println("⚠ [ERROR] Failed to send notification (Simulated)");
            return Mono.error(new RuntimeException("External Service Timeout!"));
        }

        System.out.println("✅ Notification sent successfully!");
        return Mono.empty();
    }
}
