package com.example.flowforge.travel.service;

import com.example.flowforge.travel.model.CountryInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class ExternalApiService {

    private final WebClient webClient;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    @SuppressWarnings("unchecked")
    public Mono<CountryInfo> fetchCountryInfo(String countryName) {
        return webClient.get()
                .uri("https://restcountries.com/v3.1/name/" + countryName)
                .retrieve()
                .bodyToMono(java.util.List.class)
                .map(list -> {
                    Map<String, Object> countryData = (Map<String, Object>) list.get(0);
                    java.util.List<String> capitals = (java.util.List<String>) countryData.get("capital");
                    String capital = (capitals != null && !capitals.isEmpty()) ? capitals.get(0) : "N/A";
                    
                    java.util.List<Double> latlng = (java.util.List<Double>) countryData.get("latlng");
                    double lat = (latlng != null) ? latlng.get(0) : 0.0;
                    double lng = (latlng != null) ? latlng.get(1) : 0.0;
                    
                    Map<String, Object> names = (Map<String, Object>) countryData.get("name");
                    String officialName = (String) names.get("official");
                    
                    Map<String, Object> currencies = (Map<String, Object>) countryData.get("currencies");
                    String currencyCode = "USD";
                    String currencySymbol = "$";
                    if (currencies != null && !currencies.isEmpty()) {
                        String firstKey = currencies.keySet().iterator().next();
                        currencyCode = firstKey;
                        Map<String, Object> currencyData = (Map<String, Object>) currencies.get(firstKey);
                        currencySymbol = (String) currencyData.get("symbol");
                    }

                    return CountryInfo.builder()
                            .name(officialName)
                            .capital(capital)
                            .latitude(lat)
                            .longitude(lng)
                            .currencyCode(currencyCode)
                            .currencySymbol(currencySymbol)
                            .build();
                });
    }

    @SuppressWarnings("unchecked")
    public Mono<com.example.flowforge.travel.model.WeatherInfo> fetchWeatherInfo(double lat, double lng) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.open-meteo.com")
                        .path("/v1/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lng)
                        .queryParam("current_weather", true)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(node -> {
                    Map<String, Object> current = (Map<String, Object>) node.get("current_weather");
                    return com.example.flowforge.travel.model.WeatherInfo.builder()
                            .currentTemp(Double.parseDouble(current.get("temperature").toString()))
                            .windSpeed(Double.parseDouble(current.get("windspeed").toString()))
                            .conditions("WMO Code " + current.get("weathercode"))
                            .build();
                });
    }
}
