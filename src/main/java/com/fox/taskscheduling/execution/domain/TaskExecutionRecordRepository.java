package com.fox.taskscheduling.execution.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutionRecordRepository extends JpaRepository<TaskExecutionRecord, Long> {

    List<TaskExecutionRecord> findTop50ByOrderByStartedAtDesc();

    List<TaskExecutionRecord> findTop50ByTaskCodeOrderByStartedAtDesc(String taskCode);
}
