package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@TaskHandler("audit")
public class AuditTasks {

    @FlowTask( id = "archiveAuditLog")
    public Mono<Void> archiveAuditLog(ValidationResult result) {
        return Mono.delay(Duration.ofMillis(1000))
                .doOnTerminate(() -> System.err.println("Audit process interrupted?"))
                .then();
    }
}
