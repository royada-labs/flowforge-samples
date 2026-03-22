package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

public class AuditTask extends BasicTask<ValidationResult, Void> {

    public static final TaskId ID = TaskId.of("archiveAuditLog");

    public AuditTask() {
        super(ID, ValidationResult.class, Void.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(ValidateOrderTask.ID);
    }

    @Override
    protected Mono<Void> doExecute(ValidationResult result, ReactiveExecutionContext context) {
        return Mono.delay(Duration.ofMillis(1000))
                .doOnTerminate(() -> System.err.println("Audit process finished or interrupted"))
                .then();
    }
}
