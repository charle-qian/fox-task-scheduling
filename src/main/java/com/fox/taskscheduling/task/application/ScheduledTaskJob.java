package com.fox.taskscheduling.task.application;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.fox.taskscheduling.execution.application.TaskExecutionService;
import com.fox.taskscheduling.execution.domain.TriggerType;
import com.fox.taskscheduling.task.domain.TaskDefinitionRepository;

@Component
public class ScheduledTaskJob implements Job {

    private final TaskDefinitionRepository taskRepository;
    private final TaskExecutionService executionService;

    public ScheduledTaskJob(TaskDefinitionRepository taskRepository, TaskExecutionService executionService) {
        this.taskRepository = taskRepository;
        this.executionService = executionService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskCode = context.getMergedJobDataMap().getString(TaskSchedulerService.TASK_CODE_KEY);
        taskRepository.findByCode(taskCode)
                .ifPresent(task -> executionService.runNoop(task, TriggerType.SCHEDULED));
    }
}
