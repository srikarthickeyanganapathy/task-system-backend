package com.example.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskflow.domain.TaskComment;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    // Spring Data JPA automatically derives the query for task.id
    List<TaskComment> findByTaskId(Long taskId);
}