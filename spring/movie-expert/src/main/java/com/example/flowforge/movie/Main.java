package com.example.flowforge.movie;

import com.example.flowforge.movie.model.MovieFullReport;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 [Plain Spring] Starting Movie Expert Analysis...");
        
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            FlowForgeClient client = context.getBean(FlowForgeClient.class);

            String movie = args.length > 0 ? args[0] : "Interstellar";
            System.out.println("Processing: " + movie + "\n");

            long startTime = System.currentTimeMillis();

            client.executeResult("movie-expert", movie)
                    .map(result -> (MovieFullReport) result)
                    .doOnNext(report -> {
                        long totalTime = System.currentTimeMillis() - startTime;
                        System.out.println("🎬 MOVIE EXPERT ANALYSIS READY (Plain Spring) 🎬");
                        System.out.println("---------------------------------");
                        System.out.println("🎥 Movie: " + report.getMetadata().getTitle() + " (" + report.getMetadata().getReleaseYear() + ")");
                        System.out.println("📽️ Director: " + report.getCastInfo().getDirector());
                        System.out.println("🎭 Key Cast: " + String.join(", ", report.getCastInfo().getActors()));
                        System.out.println("⭐ Rating: " + report.getRatings().getStars() + "/5 (" + report.getRatings().getReviewCount() + " reviews)");
                        System.out.println("📝 Top Critic: \"" + report.getRatings().getTopCriticComment() + "\"");
                        System.out.println("🕒 Total Workflow Execution Time: " + totalTime + "ms");
                        System.out.println("---------------------------------\n");
                    })
                    .onErrorResume(e -> {
                        System.err.println("❌ WORKFLOW FAILED: " + e.getMessage());
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            System.err.println("❌ Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("--- Plain Spring Demo Finished ---");
        System.exit(0);
    }
}
