package com.fox.taskscheduling.execution.application;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fox.taskscheduling.execution.domain.ExecutionStatus;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecord;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecordRepository;
import com.fox.taskscheduling.execution.domain.TriggerType;
import com.fox.taskscheduling.task.domain.TaskDefinition;

@Service
public class TaskExecutionService {

    private final TaskExecutionRecordRepository repository;

    public TaskExecutionService(TaskExecutionRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TaskExecutionRecord runNoop(TaskDefinition task, TriggerType triggerType) {
        TaskExecutionRecord record = start(task, triggerType);
        return complete(record);
    }

    @Transactional
    public TaskExecutionRecord start(TaskDefinition task, TriggerType triggerType) {
        TaskExecutionRecord record = new TaskExecutionRecord();
        record.setTaskCode(task.getCode());
        record.setTaskName(task.getName());
        record.setTriggerType(triggerType);
        record.setStatus(ExecutionStatus.RUNNING);
        record.setStartedAt(Instant.now());
        return repository.save(record);
    }

    @Transactional
    public TaskExecutionRecord complete(TaskExecutionRecord record) {
        Instant finishedAt = Instant.now();
        record.setFinishedAt(finishedAt);
        record.setDurationMillis(Duration.between(record.getStartedAt(), finishedAt).toMillis());
        record.setStatus(ExecutionStatus.SUCCESS);
        return repository.save(record);
    }

    @Transactional
    public TaskExecutionRecord fail(TaskExecutionRecord record, Exception exception) {
        Instant finishedAt = Instant.now();
        record.setFinishedAt(finishedAt);
        record.setDurationMillis(Duration.between(record.getStartedAt(), finishedAt).toMillis());
        record.setStatus(ExecutionStatus.FAILED);
        record.setErrorMessage(exception.getMessage());
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<TaskExecutionRecord> listRecent() {
        return repository.findTop50ByOrderByStartedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<TaskExecutionRecord> listRecentByTaskCode(String taskCode) {
        return repository.findTop50ByTaskCodeOrderByStartedAtDesc(taskCode);
    }
}
