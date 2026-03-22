package com.example.flowforge.market.workflows;

import com.example.flowforge.market.tasks.MarketIntelligenceTasks;
import org.royada.flowforge.spring.annotations.FlowWorkflow;
import org.royada.flowforge.spring.dsl.FlowDsl;
import org.royada.flowforge.spring.workflow.WorkflowDefinition;
import org.royada.flowforge.workflow.plan.WorkflowExecutionPlan;
import org.springframework.stereotype.Component;

@Component
@FlowWorkflow(id = "market-intelligence")
public class MarketIntelligenceWorkflow implements WorkflowDefinition {

    @Override
    public WorkflowExecutionPlan define(FlowDsl dsl) {
        return dsl.flow(MarketIntelligenceTasks::init)
                .then(MarketIntelligenceTasks::fetchBase)
                .then(MarketIntelligenceTasks::checkInventory)
                .fork(
                        // Branch 1: Competitors (Nested Parallelism)
                        b -> b.fork(
                                b1 -> b1.then(MarketIntelligenceTasks::fetchAmazon),
                                b1 -> b1.then(MarketIntelligenceTasks::fetchEbay)
                        ).then(MarketIntelligenceTasks::calcIndex),

                        // Branch 2: Reputation (Sequential within branch)
                        b -> b.then(MarketIntelligenceTasks::fetchSocial)
                              .then(MarketIntelligenceTasks::analyzeSentiment),

                        // Branch 3: Financial
                        b -> b.then(MarketIntelligenceTasks::fetchFinance)
                )
                // Risk Assessment (Internal skip logic)
                .then(MarketIntelligenceTasks::assessRisk)
                // Join all data
                .join(MarketIntelligenceTasks::consolidate)
                .then(MarketIntelligenceTasks::finalizeReport)
                .build();
    }
}
