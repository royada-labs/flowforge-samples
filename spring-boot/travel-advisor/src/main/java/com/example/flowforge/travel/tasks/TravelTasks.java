package com.example.flowforge.travel.tasks;

import com.example.flowforge.travel.model.CountryInfo;
import com.example.flowforge.travel.model.TravelReport;
import com.example.flowforge.travel.model.WeatherInfo;
import com.example.flowforge.travel.service.ExternalApiService;
import org.royada.flowforge.spring.annotations.FlowTask;
import org.royada.flowforge.spring.annotations.TaskHandler;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@TaskHandler("travel-tasks")
public class TravelTasks {

    private final ExternalApiService apiService;

    public TravelTasks(ExternalApiService apiService) {
        this.apiService = apiService;
    }

    @FlowTask(id = "fetchCountryInfo")
    public Mono<CountryInfo> fetchCountry(String countryName) {
        return apiService.fetchCountryInfo(countryName);
    }

    @FlowTask(id = "fetchWeatherInfo")
    public Mono<WeatherInfo> fetchWeather(CountryInfo countryInfo) {
        return apiService.fetchWeatherInfo(countryInfo.getLatitude(), countryInfo.getLongitude());
    }

    @FlowTask(id = "consolidateReport")
    public Mono<TravelReport> consolidate(WeatherInfo weather, ReactiveExecutionContext ctx) {
        // We can access results from any previous task using the task ID
        
        // However, we can manually look up the CountryInfo from the context since it was stored
        CountryInfo country = ctx.get(org.royada.flowforge.task.TaskDefinition.of(org.royada.flowforge.task.TaskId.of("fetchCountryInfo"), String.class, CountryInfo.class).outputKey()).orElseThrow();

        return Mono.just(TravelReport.builder()
                .country(country.getName())
                .capital(country.getCapital())
                .currency(country.getCurrencyCode() + " (" + country.getCurrencySymbol() + ")")
                .temperature(weather.getCurrentTemp())
                .skyCondition(weather.getConditions())
                .build());
    }
}
