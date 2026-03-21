package com.example.flowforge.ecommerce.tasks;


import com.example.flowforge.ecommerce.model.Order;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@TaskHandler("order-tasks")
public class OrderTasks {

    @FlowTask(id = "fetchOrder")
    public Mono<Order> fetchOrder(String orderId) {
        // We use the input to search for the specific order
        Order order = Order.builder()
                .id(orderId)
                .amount(99.9)
                .customerEmail("sample@mail.com")
                .status("COMPLETED")
                .build();

        return Mono.just(order);
    }
}