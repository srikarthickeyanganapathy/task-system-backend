package com.example.taskflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.security.RoleStrategy;
import com.example.taskflow.security.RoleStrategyFactory;

@Service
public class TaskWorkflowService {

    private final TaskRepository taskRepository;
    private final TaskAuditService taskAuditService;
    private final RoleStrategyFactory roleStrategyFactory;

    public List<Task> getTasksForUser(User user) {

        RoleStrategy strategy = roleStrategyFactory.getStrategy(user);

        // Director → all tasks
        if (strategy.canOverride(user)) {
            return taskRepository.findAll();
        }

        // Manager → team tasks
        if (strategy.canAssign(user)) {
            return taskRepository.findByAssignedTo_Manager_Id(user.getId());
        }

        // Employee → only own tasks
        return taskRepository.findByAssignedTo_Id(user.getId());
    }

    public TaskWorkflowService(TaskRepository taskRepository,
                               TaskAuditService taskAuditService,
                               RoleStrategyFactory roleStrategyFactory) {
        this.taskRepository = taskRepository;
        this.taskAuditService = taskAuditService;
        this.roleStrategyFactory = roleStrategyFactory;
    }

    public Task submitTask(Long taskId, User user) {
        Task task = getTask(taskId);

        if (!task.getAssignedTo().getId().equals(user.getId())) {
            throw new RuntimeException("You can submit only your own tasks");
        }

        task.setCurrentStatus("SUBMITTED");
        Task updated = taskRepository.save(task);
        taskAuditService.recordStatus(updated, "SUBMITTED", user);

        return updated;
    }

    public Task approveTask(Long taskId, User reviewer) {
        Task task = getTask(taskId);
        RoleStrategy strategy = roleStrategyFactory.getStrategy(reviewer);

        if (!strategy.canReview(reviewer, task)) {
            throw new RuntimeException("You are not authorized to approve this task");
        }

        task.setCurrentStatus("APPROVED");
        task.setReviewedBy(reviewer);
        Task updated = taskRepository.save(task);
        taskAuditService.recordStatus(updated, "APPROVED", reviewer);

        return updated;
    }

    public Task rejectTask(Long taskId, User reviewer) {
        Task task = getTask(taskId);
        RoleStrategy strategy = roleStrategyFactory.getStrategy(reviewer);

        if (!strategy.canReview(reviewer, task)) {
            throw new RuntimeException("You are not authorized to reject this task");
        }

        task.setCurrentStatus("REJECTED");
        task.setReviewedBy(reviewer);
        Task updated = taskRepository.save(task);
        taskAuditService.recordStatus(updated, "REJECTED", reviewer);

        return updated;
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
