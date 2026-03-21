package com.example.flowforge.ecommerce.workflows;

import com.example.flowforge.ecommerce.tasks.AuditTasks;
import com.example.flowforge.ecommerce.tasks.NotificationTasks;
import com.example.flowforge.ecommerce.tasks.OrderTasks;
import org.royada.flowforge.spring.annotations.FlowWorkflow;
import org.royada.flowforge.spring.dsl.FlowDsl;
import org.royada.flowforge.spring.workflow.WorkflowDefinition;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@FlowWorkflow(id = "order-process")
public class OrderProcessorWorkflow implements WorkflowDefinition {
    @Override
    public WorkflowExecutionPlan define(FlowDsl dsl) {
        return dsl.flow(OrderTasks::fetchOrder)
                .then(OrderTasks::validateOrder)
                .fork(
                        branch -> branch.then(NotificationTasks::notifyResult),
                        branch -> branch.then(AuditTasks::archiveAuditLog)
                                .withTimeout(Duration.ofMillis(500))
                )
                .join(OrderTasks::finalNotification)
                .build();
    }
}
