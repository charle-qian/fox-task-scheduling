package com.fox.taskscheduling.task.application.dto;

import java.time.Instant;

import com.fox.taskscheduling.task.domain.TaskDefinition;
import com.fox.taskscheduling.task.domain.TaskStatus;

public record TaskDefinitionView(
        Long id,
        String code,
        String name,
        String description,
        String cronExpression,
        Integer priority,
        Integer rateLimitPerMinute,
        String handlerName,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static TaskDefinitionView from(TaskDefinition task) {
        return new TaskDefinitionView(
                task.getId(),
                task.getCode(),
                task.getName(),
                task.getDescription(),
                task.getCronExpression(),
                task.getPriority(),
                task.getRateLimitPerMinute(),
                task.getHandlerName(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
