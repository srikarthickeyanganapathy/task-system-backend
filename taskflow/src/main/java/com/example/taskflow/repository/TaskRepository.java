package com.example.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskflow.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo_Id(Long userId);

    List<Task> findByCreatedBy_Id(Long userId);

    List<Task> findByReviewedBy_Id(Long userId);

    List<Task> findByAssignedTo_Manager_Id(Long managerId);
}
