package com.example.flowforge.movie.tasks;

import com.example.flowforge.movie.model.CastData;
import com.example.flowforge.movie.model.MovieFullReport;
import com.example.flowforge.movie.model.MovieMetadata;
import com.example.flowforge.movie.model.RatingData;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskDefinition;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Set;

public class ConsolidateMovieReportTask extends BasicTask<Object, MovieFullReport> {

    public static final TaskId ID = TaskId.of("generateMovieReport");

    public ConsolidateMovieReportTask() {
        super(ID, Object.class, MovieFullReport.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(FetchCastTask.ID, FetchRatingsTask.ID);
    }

    @Override
    protected Mono<MovieFullReport> doExecute(Object ignoredInput, ReactiveExecutionContext context) {
        // Retrieve all parts from context
        MovieMetadata metadata = context.get(TaskDefinition.of(FetchMovieMetadataTask.ID, String.class, MovieMetadata.class).outputKey()).orElseThrow();
        CastData cast = context.get(TaskDefinition.of(FetchCastTask.ID, MovieMetadata.class, CastData.class).outputKey()).orElseThrow();
        RatingData ratings = context.get(TaskDefinition.of(FetchRatingsTask.ID, MovieMetadata.class, RatingData.class).outputKey()).orElseThrow();

        return Mono.just(MovieFullReport.builder()
                .metadata(metadata)
                .castInfo(cast)
                .ratings(ratings)
                .generatedAt(LocalDateTime.now().toString())
                .build());
    }
}
