package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@TaskHandler("audit")
public class AuditTasks {

    @FlowTask( id = "archiveAuditLog")
    public Mono<Void> archiveAuditLog(ValidationResult in) {
        System.out.println("Validation result archived: " + in.getReason());
        return Mono.empty();
    }
}
