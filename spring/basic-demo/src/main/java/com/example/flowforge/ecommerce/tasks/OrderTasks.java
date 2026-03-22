package com.example.flowforge.ecommerce.tasks;

import com.example.flowforge.ecommerce.model.Order;
import com.example.flowforge.ecommerce.model.ValidationResult;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@TaskHandler("order-tasks")
public class OrderTasks {

    @FlowTask(id = "fetchOrder")
    public Mono<Order> fetchOrder(String orderId) {
        Order order = Order.builder()
                .id(orderId)
                .amount(99.9)
                .customerEmail("sample@mail.com")
                .status("COMPLETED")
                .build();

        return Mono.just(order);
    }

    @FlowTask(id = "validateOrder")
    public Mono<ValidationResult> validateOrder(Order order) {
        if (order.getAmount() > 0) {
            return Mono.just(new ValidationResult(true, "Validated"));
        }
        return  Mono.just(new ValidationResult(false, "Invalid Amount"));
    }

    @FlowTask(id = "finalNotification")
    public Mono<Void> finalNotification(Object in) {
        System.out.println("Pipeline finished. Sending final system alert.");
        return Mono.empty();
    }
}
