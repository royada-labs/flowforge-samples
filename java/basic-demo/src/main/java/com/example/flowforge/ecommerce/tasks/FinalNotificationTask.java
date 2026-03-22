package com.example.flowforge.ecommerce.tasks;

import org.royada.flowforge.task.BasicTask;
import org.royada.flowforge.task.TaskId;
import org.royada.flowforge.workflow.ReactiveExecutionContext;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

public class FinalNotificationTask extends BasicTask<Map<TaskId, Object>, Void> {

    @SuppressWarnings("unchecked")
    private static final Class<Map<TaskId, Object>> MAP_INPUT_TYPE =
            (Class<Map<TaskId, Object>>) (Class<?>) Map.class;

    public static final TaskId ID = TaskId.of("finalNotification");

    public FinalNotificationTask() {
        super(ID, MAP_INPUT_TYPE, Void.class);
    }

    @Override
    public Set<TaskId> dependencies() {
        return Set.of(NotificationTask.ID, AuditTask.ID);
    }

    @Override
    protected Mono<Void> doExecute(Map<TaskId, Object> input, ReactiveExecutionContext context) {
        System.out.println("Pipeline finished. Sending final system alert from pure Java.");
        return Mono.empty();
    }
}
