package com.fox.taskscheduling.task.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class TaskDefinitionRepositoryTests {

    @Autowired
    private TaskDefinitionRepository repository;

    @Test
    void savesAndLoadsTaskDefinitionByCode() {
        TaskDefinition task = new TaskDefinition();
        task.setCode("sample.cleanup");
        task.setName("Sample Cleanup");
        task.setDescription("Clean temporary data.");
        task.setCronExpression("0 0/5 * * * ?");
        task.setPriority(10);
        task.setRateLimitPerMinute(30);
        task.setHandlerName("noop");
        task.setStatus(TaskStatus.ENABLED);

        repository.save(task);

        Optional<TaskDefinition> loaded = repository.findByCode("sample.cleanup");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Sample Cleanup");
        assertThat(loaded.get().getStatus()).isEqualTo(TaskStatus.ENABLED);
        assertThat(loaded.get().getPriority()).isEqualTo(10);
    }
}
