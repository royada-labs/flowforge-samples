package com.example.flowforge.movie.workflows;

import com.example.flowforge.movie.tasks.MovieTasks;
import org.royada.flowforge.spring.annotations.FlowWorkflow;
import org.royada.flowforge.spring.dsl.FlowDsl;
import org.royada.flowforge.spring.workflow.WorkflowDefinition;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.springframework.stereotype.Component;

@Component
@FlowWorkflow(id = "movie-expert")
public class MovieExpertWorkflow implements WorkflowDefinition {

    @Override
    public WorkflowExecutionPlan define(FlowDsl dsl) {
        return dsl.flow(MovieTasks::fetchMetadata)
                .fork(
                        b -> b.then(MovieTasks::fetchCast),
                        b -> b.then(MovieTasks::fetchRatings)
                )
                .join(MovieTasks::consolidate)
                .build();
    }
}
