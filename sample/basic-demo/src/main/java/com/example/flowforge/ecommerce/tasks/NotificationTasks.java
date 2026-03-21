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

    @FlowTask( id = "notifyResult")
    public Mono<Void> notifyResult(ValidationResult in) {
        return Mono.empty()
                .delayElement(Duration.ofSeconds(1))
                .then();
    }
}
