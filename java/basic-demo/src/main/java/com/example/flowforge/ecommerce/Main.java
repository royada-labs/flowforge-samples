package com.example.flowforge.ecommerce;

import com.example.flowforge.ecommerce.tasks.*;
import org.royada.flowforge.task.TaskDescriptor;
import org.royada.flowforge.workflow.input.DefaultTaskInputResolver;
import org.royada.flowforge.workflow.monitor.AsyncLoggingWorkflowMonitor;
import org.royada.flowforge.workflow.orchestrator.ReactiveWorkflowOrchestrator;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.royada.flowforge.workflow.plan.WorkflowPlanBuilder;
import org.royada.flowforge.workflow.policy.RetryPolicy;
import org.royada.flowforge.workflow.policy.TimeoutPolicy;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting Pure Java FlowForge Demo ---");

        // 1. Instantiate tasks
        FetchOrderTask fetchTask = new FetchOrderTask();
        ValidateOrderTask validateTask = new ValidateOrderTask();
        NotificationTask notifyTask = new NotificationTask();
        AuditTask auditTask = new AuditTask();
        FinalNotificationTask finalTask = new FinalNotificationTask();

        // 2. Wrap tasks into descriptors with policies
        // No policies for basic tasks
        TaskDescriptor fetchDesc = new TaskDescriptor(fetchTask);
        TaskDescriptor validateDesc = new TaskDescriptor(validateTask);
        
        // Add 3 retries for notification
        TaskDescriptor notifyDesc = new TaskDescriptor(notifyTask, RetryPolicy.fixed(3));
        
        // Add 2000ms timeout for audit (it delays 1000ms, so it should PASS)
        TaskDescriptor auditDesc = new TaskDescriptor(auditTask, TimeoutPolicy.of(Duration.ofMillis(2000)));
        
        // Final task is optional so the workflow finished even if audit fails
        // Wait, 'optional' is a method in Task, let's make AuditTask optional in its class or via wrapper
        // Since I want to match the Spring behavior where we use .withTimeout, 
        // in pure Java we manually build the descriptors.
        
        TaskDescriptor finalDesc = new TaskDescriptor(finalTask);

        // 3. Build the execution plan
        List<TaskDescriptor> descriptors = List.of(
                fetchDesc,
                validateDesc,
                notifyDesc,
                auditDesc,
                finalDesc
        );
        WorkflowExecutionPlan plan = WorkflowPlanBuilder.buildFromDescriptors(descriptors, java.util.Map.of());

        // 4. Manual Orchestrator setup
        ReactiveWorkflowOrchestrator orchestrator = ReactiveWorkflowOrchestrator.builder()
                .taskScheduler(Schedulers.boundedElastic())
                .monitor(new AsyncLoggingWorkflowMonitor())
                .inputResolver(new DefaultTaskInputResolver())
                .build();

        // 5. Execute
        System.out.println("Executing workflow for 'order-java-789'...");
        orchestrator.execute(plan, "order-java-789")
                .doOnNext(ctx -> System.out.println("Workflow execution finished!"))
                .doOnError(e -> System.err.println("Workflow failed: " + e.getMessage()))
                .block();

        System.out.println("--- Pure Java Demo Finished ---");
        
        // Force exit since we have active schedulers
        System.exit(0);
    }
}
