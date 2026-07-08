package com.fox.taskscheduling.task.web.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fox.taskscheduling.task.application.TaskDefinitionService;
import com.fox.taskscheduling.task.application.dto.TaskDefinitionView;
import com.fox.taskscheduling.task.web.api.dto.CreateTaskRequest;
import com.fox.taskscheduling.task.web.api.dto.UpdateTaskRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    private final TaskDefinitionService taskService;

    public TaskApiController(TaskDefinitionService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    List<TaskDefinitionView> list() {
        return taskService.list();
    }

    @PostMapping
    ResponseEntity<TaskDefinitionView> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskDefinitionView created = taskService.create(request.toCommand());
        return ResponseEntity.created(URI.create("/api/tasks/" + created.code())).body(created);
    }

    @GetMapping("/{code}")
    TaskDefinitionView get(@PathVariable String code) {
        return taskService.get(code);
    }

    @PutMapping("/{code}")
    TaskDefinitionView update(@PathVariable String code, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.update(code, request.toCommand());
    }

    @PostMapping("/{code}/enable")
    TaskDefinitionView enable(@PathVariable String code) {
        return taskService.enable(code);
    }

    @PostMapping("/{code}/disable")
    TaskDefinitionView disable(@PathVariable String code) {
        return taskService.disable(code);
    }

    @PostMapping("/{code}/pause")
    TaskDefinitionView pause(@PathVariable String code) {
        return taskService.pause(code);
    }

    @PostMapping("/{code}/resume")
    TaskDefinitionView resume(@PathVariable String code) {
        return taskService.resume(code);
    }

    @PostMapping("/{code}/trigger")
    ResponseEntity<Void> trigger(@PathVariable String code) {
        taskService.trigger(code);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{code}")
    ResponseEntity<Void> delete(@PathVariable String code) {
        taskService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
