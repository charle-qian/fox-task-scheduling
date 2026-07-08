package com.fox.taskscheduling.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fox.taskscheduling.common.exception.ConflictException;
import com.fox.taskscheduling.execution.domain.ExecutionStatus;
import com.fox.taskscheduling.execution.domain.TaskExecutionRecordRepository;
import com.fox.taskscheduling.execution.domain.TriggerType;
import com.fox.taskscheduling.task.application.dto.CreateTaskCommand;
import com.fox.taskscheduling.task.application.dto.UpdateTaskCommand;
import com.fox.taskscheduling.task.domain.TaskDefinitionRepository;
import com.fox.taskscheduling.task.domain.TaskStatus;

@SpringBootTest
class TaskDefinitionServiceTests {

    @Autowired
    private TaskDefinitionService taskService;

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
    void createsTaskDefinitionAndRejectsDuplicateCode() {
        taskService.create(createCommand("sample.cleanup"));

        assertThat(taskRepository.findByCode("sample.cleanup")).isPresent();
        assertThatThrownBy(() -> taskService.create(createCommand("sample.cleanup")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("sample.cleanup");
    }

    @Test
    void managesLifecycleAndManualTrigger() {
        taskService.create(createCommand("sample.lifecycle"));

        assertThat(taskService.enable("sample.lifecycle").status()).isEqualTo(TaskStatus.ENABLED);
        assertThat(taskService.pause("sample.lifecycle").status()).isEqualTo(TaskStatus.PAUSED);
        assertThat(taskService.resume("sample.lifecycle").status()).isEqualTo(TaskStatus.ENABLED);

        taskService.trigger("sample.lifecycle");

        var records = executionRepository.findTop50ByTaskCodeOrderByStartedAtDesc("sample.lifecycle");
        assertThat(records).hasSize(1);
        assertThat(records.getFirst().getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
        assertThat(records.getFirst().getTriggerType()).isEqualTo(TriggerType.MANUAL);

        assertThat(taskService.disable("sample.lifecycle").status()).isEqualTo(TaskStatus.DISABLED);
        taskService.delete("sample.lifecycle");
        assertThat(taskRepository.findByCode("sample.lifecycle")).isEmpty();
    }

    @Test
    void updatesTaskDefinition() {
        taskService.create(createCommand("sample.update"));

        var updated = taskService.update("sample.update", new UpdateTaskCommand(
                "Updated Name",
                "Updated description",
                "0 0/10 * * * ?",
                20,
                60,
                "noop"));

        assertThat(updated.name()).isEqualTo("Updated Name");
        assertThat(updated.priority()).isEqualTo(20);
        assertThat(updated.rateLimitPerMinute()).isEqualTo(60);
    }

    private CreateTaskCommand createCommand(String code) {
        return new CreateTaskCommand(
                code,
                "Sample Task",
                "Sample description",
                "0 0/5 * * * ?",
                10,
                30,
                "noop");
    }
}
