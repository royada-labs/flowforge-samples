package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@TaskHandler("notifications")
public class NotificationTasks {

    @FlowTask(id = "notifyResult", retryMaxRetries = 3)
    public Mono<Void> notifyResult(ValidationResult result) {
        System.out.println("✅ Notification sent successfully!");
        return Mono.empty();
    }
}
