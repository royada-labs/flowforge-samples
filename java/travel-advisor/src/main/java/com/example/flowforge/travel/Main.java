package com.example.flowforge.travel;

import com.example.flowforge.travel.model.TravelReport;
import com.example.flowforge.travel.tasks.ConsolidateTask;
import com.example.flowforge.travel.tasks.FetchCountryTask;
import com.example.flowforge.travel.tasks.FetchWeatherTask;
import org.royada.flowforge.task.TaskDescriptor;
import org.royada.flowforge.workflow.orchestrator.ReactiveWorkflowOrchestrator;
import org.royada.flowforge.workflow.input.DefaultTaskInputResolver;
import org.royada.flowforge.workflow.monitor.AsyncLoggingWorkflowMonitor;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.royada.flowforge.workflow.plan.WorkflowPlanBuilder;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Pure Java] Starting Travel Advisor...");

        // 1. Setup Orchestrator
        ReactiveWorkflowOrchestrator orchestrator = ReactiveWorkflowOrchestrator.builder()
                .monitor(new AsyncLoggingWorkflowMonitor())
                .inputResolver(new DefaultTaskInputResolver())
                .taskScheduler(Schedulers.parallel())
                .build();

        // 2. Define Tasks
        FetchCountryTask fetchCountry = new FetchCountryTask();
        FetchWeatherTask fetchWeather = new FetchWeatherTask();
        ConsolidateTask consolidate = new ConsolidateTask();

        // 3. Build Plan
        WorkflowExecutionPlan plan = WorkflowPlanBuilder.buildFromDescriptors(
                List.of(
                        new TaskDescriptor(fetchCountry),
                        new TaskDescriptor(fetchWeather),
                        new TaskDescriptor(consolidate)
                ),
                java.util.Collections.emptyMap()
        );

        // 4. Execute
        String country = args.length > 0 ? args[0] : "Japan";
        System.out.println("--- Processing Travel Report for: " + country + " ---");

        orchestrator.execute("travel-advisor", plan, country)
                .map(ctx -> {
                    System.out.println("DEBUG: Workflow execution finished. Extracting result...");
                    return (TravelReport) ctx.get(consolidate.outputKey()).orElseThrow();
                })
                .doOnNext(report -> {
                    System.out.println("\n✅ Travel Report Ready (from Pure Java):");
                    System.out.println("📍 Country: " + report.getCountry());
                    System.out.println("🏙️ Capital: " + report.getCapital());
                    System.out.println("💰 Currency: " + report.getCurrency());
                    System.out.println("🌡️ Current Temp: " + report.getTemperature() + "°C");
                    System.out.println("☁️ Conditions: " + report.getSkyCondition());
                })
                .doOnError(e -> {
                    System.err.println("❌ Workflow failed!");
                    e.printStackTrace();
                })
                .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                .block();

        System.out.println("--- Pure Java Demo Finished ---");
        System.exit(0);
    }
}
