package com.fox.taskscheduling.task.application.dto;

public record UpdateTaskCommand(
        String name,
        String description,
        String cronExpression,
        Integer priority,
        Integer rateLimitPerMinute,
        String handlerName) {
}
