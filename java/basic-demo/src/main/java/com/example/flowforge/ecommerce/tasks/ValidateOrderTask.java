package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.Order;
import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.util.Set;

public class ValidateOrderTask extends BasicTask<Order, ValidationResult> {

    public static final TaskId ID = TaskId.of("validateOrder");

    public ValidateOrderTask() {
        super(ID, Order.class, ValidationResult.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(FetchOrderTask.ID);
    }

    @Override
    protected Mono<ValidationResult> doExecute(Order order, ReactiveExecutionContext context) {
        if (order.getAmount() > 0) {
            return Mono.just(new ValidationResult(true, "Validated"));
        }
        return Mono.just(new ValidationResult(false, "Invalid Amount"));
    }
}
