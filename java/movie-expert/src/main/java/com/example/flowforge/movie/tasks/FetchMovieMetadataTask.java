package com.example.flowforge.movie.tasks;

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

public class FetchMovieMetadataTask extends BasicTask<String, MovieMetadata> {

    public static final TaskId ID = TaskId.of("fetchMovieMetadata");
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "thewdb";

    public FetchMovieMetadataTask() {
        super(ID, String.class, MovieMetadata.class);
    }

    @Override
    protected Mono<MovieMetadata> doExecute(String movieTitle, ReactiveExecutionContext context) {
        String uri = String.format("http://www.omdbapi.com/?t=%s&apikey=%s", 
                movieTitle.replace(" ", "+"), API_KEY);
        
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

        return Mono.fromCompletionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response.body());
                        return Mono.just(MovieMetadata.builder()
                                .id(node.path("imdbID").asText("N/A"))
                                .title(node.path("Title").asText(movieTitle))
                                .overview(node.path("Plot").asText("No plot."))
                                .releaseYear(parseYear(node.path("Year").asText("0")))
                                .build());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    private int parseYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
