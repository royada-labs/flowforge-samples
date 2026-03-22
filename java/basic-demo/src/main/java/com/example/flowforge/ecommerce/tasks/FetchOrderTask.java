package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.Order;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

public class FetchOrderTask extends BasicTask<String, Order> {

    public static final TaskId ID = TaskId.of("fetchOrder");

    public FetchOrderTask() {
        super(ID, String.class, Order.class);
    }

    @Override
    protected Mono<Order> doExecute(String orderId, ReactiveExecutionContext context) {
        Order order = new Order(orderId, 99.9, "COMPLETED", "sample@mail.com");
        return Mono.just(order);
    }
}
