package com.example.flowforge.movie.tasks;

import com.example.flowforge.movie.model.MovieMetadata;
import com.example.flowforge.movie.model.RatingData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class FetchRatingsTask extends BasicTask<MovieMetadata, RatingData> {

    public static final TaskId ID = TaskId.of("fetchRatingInfo");
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "thewdb";

    public FetchRatingsTask() {
        super(ID, MovieMetadata.class, RatingData.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(FetchMovieMetadataTask.ID);
    }

    @Override
    protected Mono<RatingData> doExecute(MovieMetadata metadata, ReactiveExecutionContext context) {
        String uri = String.format("http://www.omdbapi.com/?i=%s&apikey=%s", 
                metadata.getId(), API_KEY);
        
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

        return Mono.fromCompletionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response.body());
                        return Mono.just(RatingData.builder()
                                .stars(node.path("imdbRating").asDouble(0.0) / 2.0)
                                .reviewCount(parseVotes(node.path("imdbVotes").asText("0")))
                                .topCriticComment(node.path("Awards").asText("No comments."))
                                .build());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    private long parseVotes(String votesStr) {
        try {
            return Long.parseLong(votesStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0L;
        }
    }
}
