package com.example.flowforge.ecommerce.service;

import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final FlowForgeClient flowForgeClient;

    public OrderService(FlowForgeClient flowForgeClient) {
        this.flowForgeClient = flowForgeClient;
    }

    public Mono<Void> processOrder(String orderId) {
        return flowForgeClient.executeResult("order-process", orderId)
                .then();
    }
}
