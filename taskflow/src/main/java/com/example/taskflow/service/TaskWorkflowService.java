package com.example.taskflow.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.taskflow.domain.ChecklistItem;
import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskComment;
import com.example.taskflow.domain.User;
import com.example.taskflow.repository.ChecklistItemRepository;
import com.example.taskflow.repository.TaskCommentRepository;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.security.RoleStrategy;
import com.example.taskflow.security.RoleStrategyFactory;

import jakarta.transaction.Transactional;

@Service
public class TaskWorkflowService {

    private final TaskRepository taskRepository;
    private final TaskAuditService taskAuditService;
    private final RoleStrategyFactory roleStrategyFactory;
    private final TaskCommentRepository taskCommentRepository;
    private final ChecklistItemRepository checklistItemRepository;

    public TaskWorkflowService(TaskRepository taskRepository,
                               TaskAuditService taskAuditService,
                               RoleStrategyFactory roleStrategyFactory,
                               TaskCommentRepository taskCommentRepository,
                               ChecklistItemRepository checklistItemRepository) {
        this.taskRepository = taskRepository;
        this.taskAuditService = taskAuditService;
        this.roleStrategyFactory = roleStrategyFactory;
        this.taskCommentRepository = taskCommentRepository;
        this.checklistItemRepository = checklistItemRepository;
    }

    /**
     * Determines which tasks a user can see based on their Role Strategy.
     */
    public List<Task> getTasksForUser(User user) {
        RoleStrategy strategy = roleStrategyFactory.getStrategy(user);

        if (strategy.canOverride(user)) {
            return taskRepository.findAll(); // Director sees all
        }

        if (strategy.canAssign(user)) {
            return taskRepository.findByAssignedToOrCreatedBy(user); // Manager sees team's + own
        }

        return taskRepository.findByAssignedTo(user); // Employee sees only own
    }

    public Task submitTask(Long taskId, User user) {
        Task task = getTask(taskId);
        
        // Ensure only the assignee can submit
        if (!task.getAssignedTo().getId().equals(user.getId())) {
            throw new RuntimeException("You can only submit tasks assigned to you.");
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
            throw new RuntimeException("You are not authorized to review this task.");
        }

        validateReviewer(reviewer, task);

        task.setCurrentStatus("APPROVED");
        task.setReviewedBy(reviewer); // Store who approved it
        
        Task updated = taskRepository.save(task);
        taskAuditService.recordStatus(updated, "APPROVED", reviewer);
        return updated;
    }

    public Task rejectTask(Long taskId, User reviewer, String reason) {
        Task task = getTask(taskId);
        validateReviewer(reviewer, task);

        task.setCurrentStatus("REJECTED");
        task.setReviewedBy(reviewer);
        
        Task updated = taskRepository.save(task);

        if (reason != null && !reason.trim().isEmpty()) {
            addComment(taskId, reviewer, "Rejection Reason: " + reason);
        }

        taskAuditService.recordStatus(updated, "REJECTED", reviewer);
        return updated;
    }

    // --- Helper Methods ---

    private void validateReviewer(User reviewer, Task task) {
        RoleStrategy strategy = roleStrategyFactory.getStrategy(reviewer);
        if (!strategy.canReview(reviewer, task)) {
            throw new RuntimeException("You are not authorized to review this task.");
        }
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
    }

    // --- Comments & Checklists ---

    public TaskComment addComment(Long taskId, User user, String text) {
        Task task = getTask(taskId);
        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setComment(text);
        comment.setCreatedAt(LocalDateTime.now());
        return taskCommentRepository.save(comment);
    }

    public List<TaskComment> getComments(Long taskId) {
        return taskCommentRepository.findByTaskId(taskId);
    }

    @Transactional
    public ChecklistItem addChecklistItem(Long taskId, String text) {
        Task task = getTask(taskId);
        ChecklistItem item = new ChecklistItem();
        item.setTask(task);
        item.setText(text);
        item.setIsCompleted(false);
        return checklistItemRepository.save(item);
    }

    @Transactional
    public ChecklistItem toggleChecklistItem(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));
        item.setIsCompleted(!item.getIsCompleted());
        return checklistItemRepository.save(item);
    }
}