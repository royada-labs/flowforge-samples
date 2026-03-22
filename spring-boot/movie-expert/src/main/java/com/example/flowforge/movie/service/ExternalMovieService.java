package com.example.flowforge.movie.service;

import com.example.flowforge.movie.model.CastData;
import com.example.flowforge.movie.model.MovieMetadata;
import com.example.flowforge.movie.model.RatingData;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ExternalMovieService {

    private final WebClient webClient = WebClient.create("http://www.omdbapi.com");
    private final String API_KEY = "thewdb"; // Demo key

    public Mono<MovieMetadata> getMetadata(String title) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> MovieMetadata.builder()
                        .id(node.path("imdbID").asText("N/A"))
                        .title(node.path("Title").asText(title))
                        .overview(node.path("Plot").asText("No overview available."))
                        .releaseYear(parseYear(node.path("Year").asText("0")))
                        .build())
                .onErrorResume(e -> Mono.just(MovieMetadata.builder()
                        .id("MOCK-001")
                        .title(title)
                        .overview("FALLBACK: API Error or Key Expired.")
                        .releaseYear(2024)
                        .build()));
    }

    public Mono<CastData> getCast(String movieId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("i", movieId)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    String actorsStr = node.path("Actors").asText("");
                    return CastData.builder()
                            .director(node.path("Director").asText("Unknown"))
                            .actors(Arrays.asList(actorsStr.split(", ")))
                            .build();
                })
                .onErrorResume(e -> Mono.just(CastData.builder()
                        .director("Unknown")
                        .actors(Arrays.asList("Fallback Actor"))
                        .build()));
    }

    public Mono<RatingData> getRatings(String movieId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("i", movieId)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> RatingData.builder()
                        .stars(node.path("imdbRating").asDouble(0.0) / 2.0) // Normalize to /5
                        .reviewCount(parseVotes(node.path("imdbVotes").asText("0")))
                        .topCriticComment(node.path("Awards").asText("No comments."))
                        .build())
                .onErrorResume(e -> Mono.just(RatingData.builder()
                        .stars(0.0)
                        .reviewCount(0L)
                        .topCriticComment("Fallback: API error.")
                        .build()));
    }

    private int parseYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseVotes(String votesStr) {
        try {
            return Long.parseLong(votesStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0L;
        }
    }
}
