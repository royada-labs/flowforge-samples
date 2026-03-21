package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.Order;
import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.royada.flowforge.task.TaskDefinition;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@TaskHandler( "notifications")
public class NotificationTasks {

    private static final TaskDefinition<Order, ValidationResult> VALIDATION_RESULT =
            TaskDefinition.of("validateOrder", Order.class, ValidationResult.class);

    @FlowTask(id = "notifyResult", retryMaxRetries = 3)
    public Mono<Void> notifyResult(ValidationResult result, ReactiveExecutionContext ctx) {

        ValidationResult res = ctx.get(VALIDATION_RESULT.outputKey())
                .orElseThrow(() -> new IllegalStateException("Validation is required!"));

        if (res.isValid()) {
            System.out.println("💌 Order valid! Sending notification...");
        }
        return Mono.empty();
    }
}
