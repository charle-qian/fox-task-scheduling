package com.fox.taskscheduling.task.application.dto;

public record CreateTaskCommand(
        String code,
        String name,
        String description,
        String cronExpression,
        Integer priority,
        Integer rateLimitPerMinute,
        String handlerName) {
}
