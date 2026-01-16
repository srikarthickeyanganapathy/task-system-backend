package com.example.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Used by Employees to see only their assigned tasks
    List<Task> findByAssignedTo(User user);

    // Used by Managers to see tasks they assigned OR tasks assigned to them
    // This supports the "Review" workflow where a Manager needs to see tasks they created
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user OR t.createdBy = :user")
    List<Task> findByAssignedToOrCreatedBy(@Param("user") User user);
}