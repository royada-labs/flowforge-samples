package com.example.flowforge.movie.tasks;

import com.example.flowforge.movie.model.CastData;
import com.example.flowforge.movie.model.MovieMetadata;
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
import java.util.Arrays;
import java.util.Set;

public class FetchCastTask extends BasicTask<MovieMetadata, CastData> {

    public static final TaskId ID = TaskId.of("fetchCastInfo");
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "thewdb";

    public FetchCastTask() {
        super(ID, MovieMetadata.class, CastData.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(FetchMovieMetadataTask.ID);
    }

    @Override
    protected Mono<CastData> doExecute(MovieMetadata metadata, ReactiveExecutionContext context) {
        String uri = String.format("http://www.omdbapi.com/?i=%s&apikey=%s", 
                metadata.getId(), API_KEY);
        
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

        return Mono.fromCompletionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response.body());
                        String actorsStr = node.path("Actors").asText("");
                        return Mono.just(CastData.builder()
                                .director(node.path("Director").asText("Unknown"))
                                .actors(Arrays.asList(actorsStr.split(", ")))
                                .build());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}
