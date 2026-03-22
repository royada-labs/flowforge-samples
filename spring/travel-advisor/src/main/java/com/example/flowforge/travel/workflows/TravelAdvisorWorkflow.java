package com.example.flowforge.travel.workflows;

import com.example.flowforge.travel.tasks.TravelTasks;
import org.royada.flowforge.spring.annotations.FlowWorkflow;
import org.royada.flowforge.spring.dsl.FlowDsl;
import org.royada.flowforge.spring.workflow.WorkflowDefinition;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.springframework.stereotype.Component;

@Component
@FlowWorkflow(id = "travel-advisor")
public class TravelAdvisorWorkflow implements WorkflowDefinition {

    @Override
    public WorkflowExecutionPlan define(FlowDsl dsl) {
        return dsl.flow(TravelTasks::fetchCountry)
                .then(TravelTasks::fetchWeather)
                .then(TravelTasks::consolidate)
                .build();
    }
}
