package com.fox.taskscheduling.task.web.api.dto;

import java.time.Instant;

import com.fox.taskscheduling.execution.domain.ExecutionStatus;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecord;
import com.fox.taskscheduling.execution.domain.TriggerType;

public record ExecutionRecordResponse(
        Long id,
        String taskCode,
        String taskName,
        ExecutionStatus status,
        TriggerType triggerType,
        Instant startedAt,
        Instant finishedAt,
        Long durationMillis,
        String errorMessage) {

    public static ExecutionRecordResponse from(TaskExecutionRecord record) {
        return new ExecutionRecordResponse(
                record.getId(),
                record.getTaskCode(),
                record.getTaskName(),
                record.getStatus(),
                record.getTriggerType(),
                record.getStartedAt(),
                record.getFinishedAt(),
                record.getDurationMillis(),
                record.getErrorMessage());
    }
}
