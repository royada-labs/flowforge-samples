package com.example.flowforge.travel.tasks;

import com.example.flowforge.travel.model.CountryInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;

public class FetchCountryTask extends BasicTask<String, CountryInfo> {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FetchCountryTask() {
        super(org.royada.flowforge.task.TaskId.of("fetchCountryInfo"), String.class, CountryInfo.class);
    }

    @Override
    public Mono<CountryInfo> doExecute(String countryName, ReactiveExecutionContext context) {
        System.out.println("DEBUG: FetchCountryTask.doExecute: " + countryName);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://restcountries.com/v3.1/name/" + countryName))
                .GET()
                .build();

        return Mono.fromCompletionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(response -> {
                    try {
                        JsonNode node = objectMapper.readTree(response.body());
                        JsonNode countryNode = node.get(0);
                        
                        JsonNode capitalArray = countryNode.get("capital");
                        String capital = (capitalArray != null && capitalArray.isArray()) ? capitalArray.get(0).asText() : "N/A";
                        
                        JsonNode latlng = countryNode.get("latlng");
                        double lat = latlng != null ? latlng.get(0).asDouble() : 0.0;
                        double lng = latlng != null ? latlng.get(1).asDouble() : 0.0;
                        
                        String officialName = countryNode.get("name").get("official").asText();
                        
                        JsonNode currencies = countryNode.get("currencies");
                        String currencyCode = "USD";
                        String currencySymbol = "$";
                        if (currencies != null && currencies.isObject()) {
                            Iterator<Map.Entry<String, JsonNode>> it = currencies.fields();
                            if (it.hasNext()) {
                                Map.Entry<String, JsonNode> first = it.next();
                                currencyCode = first.getKey();
                                currencySymbol = first.getValue().get("symbol").asText();
                            }
                        }

                        return Mono.just(CountryInfo.builder()
                                .name(officialName)
                                .capital(capital)
                                .latitude(lat)
                                .longitude(lng)
                                .currencyCode(currencyCode)
                                .currencySymbol(currencySymbol)
                                .build());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}
