package com.example.flowforge.travel.tasks;

import com.example.flowforge.travel.model.CountryInfo;
import com.example.flowforge.travel.model.WeatherInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FetchWeatherTask extends BasicTask<CountryInfo, WeatherInfo> {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FetchWeatherTask() {
        super(org.royada.flowforge.task.TaskId.of("fetchWeatherInfo"), CountryInfo.class, WeatherInfo.class);
    }

    @Override
    public java.util.Set<org.royada.flowforge.task.TaskId> dependencies() {
        return java.util.Set.of(org.royada.flowforge.task.TaskId.of("fetchCountryInfo"));
    }

    @Override
    public Mono<WeatherInfo> doExecute(CountryInfo countryInfo, ReactiveExecutionContext context) {
        String uri = String.format(java.util.Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%.2f&longitude=%.2f&current_weather=true",
                countryInfo.getLatitude(), countryInfo.getLongitude());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        return Mono.fromCompletionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response.body());
                        JsonNode current = node.get("current_weather");
                        return Mono.just(WeatherInfo.builder()
                                .currentTemp(current.get("temperature").asDouble())
                                .windSpeed(current.get("windspeed").asDouble())
                                .conditions("WMO Code " + current.get("weathercode").asInt())
                                .build());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}
