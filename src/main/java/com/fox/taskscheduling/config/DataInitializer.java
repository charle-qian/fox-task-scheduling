package com.fox.taskscheduling.config;

import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fox.taskscheduling.execution.domain.ExecutionStatus;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecord;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecordRepository;
import com.fox.taskscheduling.execution.domain.TriggerType;
import com.fox.taskscheduling.task.domain.TaskDefinition;
import com.fox.taskscheduling.task.domain.TaskDefinitionRepository;
import com.fox.taskscheduling.task.domain.TaskStatus;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner seedData(
            TaskDefinitionRepository taskRepository,
            TaskExecutionRecordRepository executionRepository) {
        return args -> {
            if (!taskRepository.existsByCode("sample.cleanup")) {
                taskRepository.save(task(
                        "sample.cleanup",
                        "Sample Cleanup",
                        "Clean temporary data every five minutes.",
                        "0 0/5 * * * ?",
                        10,
                        30,
                        TaskStatus.ENABLED));
            }
            if (!taskRepository.existsByCode("sample.report")) {
                taskRepository.save(task(
                        "sample.report",
                        "Sample Report",
                        "Generate a lightweight operational report.",
                        "0 0 9 * * ?",
                        5,
                        10,
                        TaskStatus.DISABLED));
            }
            if (executionRepository.count() == 0) {
                Instant startedAt = Instant.now().minusSeconds(90);
                Instant finishedAt = startedAt.plusSeconds(2);
                TaskExecutionRecord record = new TaskExecutionRecord();
                record.setTaskCode("sample.cleanup");
                record.setTaskName("Sample Cleanup");
                record.setStatus(ExecutionStatus.SUCCESS);
                record.setTriggerType(TriggerType.SYSTEM);
                record.setStartedAt(startedAt);
                record.setFinishedAt(finishedAt);
                record.setDurationMillis(Duration.between(startedAt, finishedAt).toMillis());
                executionRepository.save(record);
            }
        };
    }

    private TaskDefinition task(
            String code,
            String name,
            String description,
            String cronExpression,
            Integer priority,
            Integer rateLimitPerMinute,
            TaskStatus status) {
        TaskDefinition task = new TaskDefinition();
        task.setCode(code);
        task.setName(name);
        task.setDescription(description);
        task.setCronExpression(cronExpression);
        task.setPriority(priority);
        task.setRateLimitPerMinute(rateLimitPerMinute);
        task.setHandlerName("noop");
        task.setStatus(status);
        return task;
    }
}
