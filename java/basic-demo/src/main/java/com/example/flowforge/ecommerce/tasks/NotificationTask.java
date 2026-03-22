package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.util.Set;

public class NotificationTask extends BasicTask<ValidationResult, Void> {

    public static final TaskId ID = TaskId.of("notifyResult");

    public NotificationTask() {
        super(ID, ValidationResult.class, Void.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(ValidateOrderTask.ID);
    }

    @Override
    protected Mono<Void> doExecute(ValidationResult result, ReactiveExecutionContext context) {
        System.out.println("✅ Notification sent successfully!");
        return Mono.empty();
    }
}
