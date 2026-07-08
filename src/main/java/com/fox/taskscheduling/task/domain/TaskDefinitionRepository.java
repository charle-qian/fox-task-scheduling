package com.fox.taskscheduling.task.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {

    Optional<TaskDefinition> findByCode(String code);

    boolean existsByCode(String code);

    void deleteByCode(String code);

    List<TaskDefinition> findByStatus(TaskStatus status);
}
