package com.example.flowforge.travel.tasks;

import com.example.flowforge.travel.model.CountryInfo;
import com.example.flowforge.travel.model.TravelReport;
import com.example.flowforge.travel.model.WeatherInfo;
import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskDefinition;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

public class ConsolidateTask extends BasicTask<WeatherInfo, TravelReport> {

    public ConsolidateTask() {
        super(TaskId.of("consolidateReport"), WeatherInfo.class, TravelReport.class);
    }

    @Override
    public java.util.Set<org.royada.flowforge.task.TaskId> dependencies() {
        return java.util.Set.of(org.royada.flowforge.task.TaskId.of("fetchWeatherInfo"));
    }

    @Override
    public Mono<TravelReport> doExecute(WeatherInfo weather, ReactiveExecutionContext context) {
        // Retrieve CountryInfo from the context
        CountryInfo country = context.get(TaskDefinition.of(TaskId.of("fetchCountryInfo"), String.class, CountryInfo.class).outputKey()).orElseThrow();

        return Mono.just(TravelReport.builder()
                .country(country.getName())
                .capital(country.getCapital())
                .currency(country.getCurrencyCode() + " (" + country.getCurrencySymbol() + ")")
                .temperature(weather.getCurrentTemp())
                .skyCondition(weather.getConditions())
                .build());
    }
}
