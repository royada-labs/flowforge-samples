package com.example.flowforge.ecommerce.workflows;

import com.example.flowforge.ecommerce.tasks.OrderTasks;
import org.royada.flowforge.spring.annotations.FlowWorkflow;
import org.royada.flowforge.spring.dsl.FlowDsl;
import org.royada.flowforge.spring.workflow.WorkflowDefinition;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.springframework.stereotype.Component;

@Component
@FlowWorkflow(id = "order-process")
public class OrderProcessorWorkflow implements WorkflowDefinition {
    @Override
    public WorkflowExecutionPlan define(FlowDsl dsl) {
        return dsl.flow(OrderTasks::fetchOrder)
                .build();
    }
}
