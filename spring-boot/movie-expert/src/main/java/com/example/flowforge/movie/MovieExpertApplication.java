package com.example.flowforge.movie;

import com.example.flowforge.movie.model.MovieFullReport;
import org.royada.flowforge.api.FlowForgeClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieExpertApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieExpertApplication.class, args);
    }

    @Bean
    public CommandLineRunner runWorkflow(FlowForgeClient client) {
        return args -> {
            String movie = args.length > 0 ? args[0] : "Inception";
            System.out.println("\n🚀 [Movie Expert] Starting Analysis Workflow for: " + movie);

            long startTime = System.currentTimeMillis();

            client.executeResult("movie-expert", movie)
                    .map(result -> (MovieFullReport) result)
                    .doOnNext(report -> {
                        long totalTime = System.currentTimeMillis() - startTime;
                        System.out.println("\n🎬 MOVIE EXPERT ANALYSIS READY 🎬");
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
        };
    }
}
