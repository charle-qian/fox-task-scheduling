package com.fox.taskscheduling.task.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fox.taskscheduling.common.exception.ConflictException;
import com.fox.taskscheduling.common.exception.ResourceNotFoundException;
import com.fox.taskscheduling.execution.application.TaskExecutionService;
import com.fox.taskscheduling.execution.domain.TriggerType;
import com.fox.taskscheduling.task.application.dto.CreateTaskCommand;
import com.fox.taskscheduling.task.application.dto.TaskDefinitionView;
import com.fox.taskscheduling.task.application.dto.UpdateTaskCommand;
import com.fox.taskscheduling.task.domain.TaskDefinition;
import com.fox.taskscheduling.task.domain.TaskDefinitionRepository;
import com.fox.taskscheduling.task.domain.TaskStatus;

@Service
public class TaskDefinitionService {

    private final TaskDefinitionRepository repository;
    private final TaskSchedulerService schedulerService;
    private final TaskExecutionService executionService;

    public TaskDefinitionService(
            TaskDefinitionRepository repository,
            TaskSchedulerService schedulerService,
            TaskExecutionService executionService) {
        this.repository = repository;
        this.schedulerService = schedulerService;
        this.executionService = executionService;
    }

    @Transactional(readOnly = true)
    public List<TaskDefinitionView> list() {
        return repository.findAll().stream()
                .map(TaskDefinitionView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDefinitionView get(String code) {
        return TaskDefinitionView.from(findByCode(code));
    }

    @Transactional
    public TaskDefinitionView create(CreateTaskCommand command) {
        if (repository.existsByCode(command.code())) {
            throw new ConflictException("Task code already exists: " + command.code());
        }
        TaskDefinition task = new TaskDefinition();
        task.setCode(command.code());
        apply(task, command.name(), command.description(), command.cronExpression(),
                command.priority(), command.rateLimitPerMinute(), command.handlerName());
        task.setStatus(TaskStatus.DISABLED);
        return TaskDefinitionView.from(repository.save(task));
    }

    @Transactional
    public TaskDefinitionView update(String code, UpdateTaskCommand command) {
        TaskDefinition task = findByCode(code);
        apply(task, command.name(), command.description(), command.cronExpression(),
                command.priority(), command.rateLimitPerMinute(), command.handlerName());
        TaskDefinition saved = repository.save(task);
        if (saved.getStatus() == TaskStatus.ENABLED) {
            schedulerService.schedule(saved);
        }
        return TaskDefinitionView.from(saved);
    }

    @Transactional
    public TaskDefinitionView enable(String code) {
        TaskDefinition task = findByCode(code);
        task.setStatus(TaskStatus.ENABLED);
        TaskDefinition saved = repository.save(task);
        schedulerService.schedule(saved);
        return TaskDefinitionView.from(saved);
    }

    @Transactional
    public TaskDefinitionView disable(String code) {
        TaskDefinition task = findByCode(code);
        task.setStatus(TaskStatus.DISABLED);
        TaskDefinition saved = repository.save(task);
        schedulerService.unschedule(code);
        return TaskDefinitionView.from(saved);
    }

    @Transactional
    public TaskDefinitionView pause(String code) {
        TaskDefinition task = findByCode(code);
        task.setStatus(TaskStatus.PAUSED);
        TaskDefinition saved = repository.save(task);
        schedulerService.pause(code);
        return TaskDefinitionView.from(saved);
    }

    @Transactional
    public TaskDefinitionView resume(String code) {
        TaskDefinition task = findByCode(code);
        task.setStatus(TaskStatus.ENABLED);
        TaskDefinition saved = repository.save(task);
        schedulerService.schedule(saved);
        schedulerService.resume(code);
        return TaskDefinitionView.from(saved);
    }

    @Transactional
    public void trigger(String code) {
        TaskDefinition task = findByCode(code);
        executionService.runNoop(task, TriggerType.MANUAL);
    }

    @Transactional
    public void delete(String code) {
        TaskDefinition task = findByCode(code);
        schedulerService.unschedule(code);
        repository.delete(task);
    }

    private TaskDefinition findByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + code));
    }

    private void apply(
            TaskDefinition task,
            String name,
            String description,
            String cronExpression,
            Integer priority,
            Integer rateLimitPerMinute,
            String handlerName) {
        task.setName(name);
        task.setDescription(description);
        task.setCronExpression(cronExpression);
        task.setPriority(priority);
        task.setRateLimitPerMinute(rateLimitPerMinute);
        task.setHandlerName(handlerName);
    }
}
