package com.fox.taskscheduling.task.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fox.taskscheduling.execution.domain.TaskExecutionRecordRepository;
import com.fox.taskscheduling.task.domain.TaskDefinitionRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskDefinitionRepository taskRepository;

    @Autowired
    private TaskExecutionRecordRepository executionRepository;

    @AfterEach
    void cleanUp() {
        executionRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    void managesTaskThroughApi() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(user("admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson("api.cleanup")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("api.cleanup"))
                .andExpect(jsonPath("$.status").value("DISABLED"));

        mockMvc.perform(get("/api/tasks").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("api.cleanup"));

        mockMvc.perform(post("/api/tasks/api.cleanup/enable").with(user("admin")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENABLED"));

        mockMvc.perform(post("/api/tasks/api.cleanup/pause").with(user("admin")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));

        mockMvc.perform(post("/api/tasks/api.cleanup/resume").with(user("admin")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENABLED"));

        mockMvc.perform(post("/api/tasks/api.cleanup/trigger").with(user("admin")).with(csrf()))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/api/tasks/api.cleanup/executions").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));

        mockMvc.perform(delete("/api/tasks/api.cleanup").with(user("admin")).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsConflictForDuplicateCode() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(user("admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson("api.duplicate")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tasks")
                        .with(user("admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson("api.duplicate")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Task code already exists: api.duplicate"));
    }

    @Test
    void returnsValidationErrorsAndNotFound() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(user("admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.code").exists());

        mockMvc.perform(get("/api/tasks/missing").with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found: missing"));
    }

    @Test
    void rejectsUnauthenticatedApiRequests() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    private String createJson(String code) {
        return """
                {
                  "code": "%s",
                  "name": "API Cleanup",
                  "description": "Cleanup through API",
                  "cronExpression": "0 0/5 * * * ?",
                  "priority": 10,
                  "rateLimitPerMinute": 30,
                  "handlerName": "noop"
                }
                """.formatted(code);
    }
}
