package com.fox.taskscheduling.task.web.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fox.taskscheduling.execution.application.TaskExecutionService;
import com.fox.taskscheduling.task.web.api.dto.ExecutionRecordResponse;

@RestController
@RequestMapping("/api")
public class ExecutionApiController {

    private final TaskExecutionService executionService;

    public ExecutionApiController(TaskExecutionService executionService) {
        this.executionService = executionService;
    }

    @GetMapping("/executions")
    List<ExecutionRecordResponse> listRecent() {
        return executionService.listRecent().stream()
                .map(ExecutionRecordResponse::from)
                .toList();
    }

    @GetMapping("/tasks/{code}/executions")
    List<ExecutionRecordResponse> listByTask(@PathVariable String code) {
        return executionService.listRecentByTaskCode(code).stream()
                .map(ExecutionRecordResponse::from)
                .toList();
    }
}
