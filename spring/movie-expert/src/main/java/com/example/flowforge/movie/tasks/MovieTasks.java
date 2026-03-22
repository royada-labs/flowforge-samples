package com.example.flowforge.movie.tasks;

import com.example.flowforge.movie.model.CastData;
import com.example.flowforge.movie.model.MovieFullReport;
import com.example.flowforge.movie.model.MovieMetadata;
import com.example.flowforge.movie.model.RatingData;
import com.example.flowforge.movie.service.ExternalMovieService;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.royada.flowforge.task.TaskDefinition;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Component
@TaskHandler("movie-tasks")
public class MovieTasks {

    private final ExternalMovieService movieService;

    public MovieTasks(ExternalMovieService movieService) {
        this.movieService = movieService;
    }

    @FlowTask(id = "fetchMovieMetadata")
    public Mono<MovieMetadata> fetchMetadata(String movieTitle) {
        System.out.println("🎬 [Task] Starting Metadata Fetch for: " + movieTitle);
        return movieService.getMetadata(movieTitle);
    }

    @FlowTask(id = "fetchCastInfo")
    public Mono<CastData> fetchCast(MovieMetadata metadata) {
        System.out.println("👥 [Task] Starting Parallel Cast Fetch for: " + metadata.getId());
        return movieService.getCast(metadata.getId());
    }

    @FlowTask(id = "fetchRatingInfo")
    public Mono<RatingData> fetchRatings(MovieMetadata metadata) {
        System.out.println("⭐ [Task] Starting Parallel Rating Fetch for: " + metadata.getId());
        return movieService.getRatings(metadata.getId());
    }

    @FlowTask(id = "generateMovieReport")
    public Mono<MovieFullReport> consolidate(Object ignoredInput, ReactiveExecutionContext context) {
        System.out.println("🧩 [Task] Consolidating Full Movie Report...");
        
        // Retrieve outputs from context using TaskDefinitions
        MovieMetadata metadata = context.get(TaskDefinition.of(TaskId.of("fetchMovieMetadata"), String.class, MovieMetadata.class).outputKey()).orElseThrow();
        CastData cast = context.get(TaskDefinition.of(TaskId.of("fetchCastInfo"), MovieMetadata.class, CastData.class).outputKey()).orElseThrow();
        RatingData ratings = context.get(TaskDefinition.of(TaskId.of("fetchRatingInfo"), MovieMetadata.class, RatingData.class).outputKey()).orElseThrow();

        return Mono.just(MovieFullReport.builder()
                .metadata(metadata)
                .castInfo(cast)
                .ratings(ratings)
                .generatedAt(LocalDateTime.now().toString())
                .build());
    }
}
