package com.example.flowforge.ecommerce;

import com.example.flowforge.ecommerce.service.OrderService;
import org.royada.flowforge.registry.WorkflowPlanRegistry;
import org.royada.flowforge.validation.FlowValidationResult;
import org.royada.flowforge.visualization.FlowVisualization;
import org.royada.flowforge.visualization.FlowVisualizer;
import org.royada.flowforge.workflow.orchestrator.ReactiveWorkflowOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceDemoApplication implements CommandLineRunner {

	@Autowired
	OrderService orderService;

	@Autowired
	private WorkflowPlanRegistry registry;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(orderService.processOrder("order-process").block());
		String res = registry.find("order-process")
				.map(plan -> FlowVisualizer.visualize(plan, FlowValidationResult.of(java.util.List.of())).toMermaid())
				.orElse("Workflow not found!");
			System.out.println(res);
	}
}
