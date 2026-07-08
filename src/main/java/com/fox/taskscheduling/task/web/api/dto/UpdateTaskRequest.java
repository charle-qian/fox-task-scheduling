package com.fox.taskscheduling.task.web.api.dto;

import com.fox.taskscheduling.task.application.dto.UpdateTaskCommand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 500) String description,
        @NotBlank @Size(max = 120) String cronExpression,
        @NotNull @Min(0) @Max(1000) Integer priority,
        @Min(1) @Max(1_000_000) Integer rateLimitPerMinute,
        @NotBlank @Size(max = 120) String handlerName) {

    public UpdateTaskCommand toCommand() {
        return new UpdateTaskCommand(name, description, cronExpression, priority, rateLimitPerMinute, handlerName);
    }
}
