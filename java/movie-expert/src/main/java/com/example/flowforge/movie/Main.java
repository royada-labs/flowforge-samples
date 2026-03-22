package com.example.flowforge.movie;

import com.example.flowforge.movie.model.MovieFullReport;
import com.example.flowforge.movie.tasks.ConsolidateMovieReportTask;
import com.example.flowforge.movie.tasks.FetchCastTask;
import com.example.flowforge.movie.tasks.FetchMovieMetadataTask;
import com.example.flowforge.movie.tasks.FetchRatingsTask;
import org.royada.flowforge.task.TaskDescriptor;
import org.royada.flowforge.workflow.orchestrator.ReactiveWorkflowOrchestrator;
import org.royada.flowforge.workflow.input.DefaultTaskInputResolver;
import org.royada.flowforge.workflow.monitor.AsyncLoggingWorkflowMonitor;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.royada.flowforge.workflow.plan.WorkflowPlanBuilder;
import reactor.core.scheduler.Schedulers;
import java.util.List;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Pure Java] Starting Movie Expert Analysis... (Parallel Workflow)");
        
        // 1. Setup Orchestrator
        ReactiveWorkflowOrchestrator orchestrator = ReactiveWorkflowOrchestrator.builder()
                .monitor(new AsyncLoggingWorkflowMonitor())
                .inputResolver(new DefaultTaskInputResolver())
                .taskScheduler(Schedulers.parallel())
                .build();

        // 2. Define Tasks
        FetchMovieMetadataTask metadataTask = new FetchMovieMetadataTask();
        FetchCastTask castTask = new FetchCastTask();
        FetchRatingsTask ratingsTask = new FetchRatingsTask();
        ConsolidateMovieReportTask consolidateTask = new ConsolidateMovieReportTask();

        // 3. Build Plan
        WorkflowExecutionPlan plan = WorkflowPlanBuilder.buildFromDescriptors(
                List.of(
                        new TaskDescriptor(metadataTask),
                        new TaskDescriptor(castTask),
                        new TaskDescriptor(ratingsTask),
                        new TaskDescriptor(consolidateTask)
                ),
                Collections.emptyMap()
        );

        // 4. Execute
        String movie = args.length > 0 ? args[0] : "Interstellar";
        System.out.println("Processing: " + movie + "\n");

        long startTime = System.currentTimeMillis();

        orchestrator.execute("movie-expert", plan, movie)
                .map(ctx -> (MovieFullReport) ctx.get(consolidateTask.outputKey()).orElseThrow())
                .doOnNext(report -> {
                    long totalTime = System.currentTimeMillis() - startTime;
                    System.out.println("\n🎬 MOVIE EXPERT ANALYSIS READY (Pure Java) 🎬");
                    System.out.println("---------------------------------");
                    System.out.println("🎥 Movie: " + report.getMetadata().getTitle() + " (" + report.getMetadata().getReleaseYear() + ")");
                    System.out.println("📽️ Director: " + report.getCastInfo().getDirector());
                    System.out.println("🎭 Key Cast: " + String.join(", ", report.getCastInfo().getActors()));
                    System.out.println("⭐ Rating: " + report.getRatings().getStars() + "/5 (" + report.getRatings().getReviewCount() + " reviews)");
                    System.out.println("📝 Top Critic: \"" + report.getRatings().getTopCriticComment() + "\"");
                    System.out.println("🕒 Total Workflow Execution Time: " + totalTime + "ms");
                    System.out.println("---------------------------------\n");
                })
                .doOnError(e -> {
                    System.err.println("❌ WORKFLOW FAILED!");
                    e.printStackTrace();
                })
                .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                .block();

        System.out.println("--- Pure Java Demo Finished ---");
        System.exit(0);
    }
}
