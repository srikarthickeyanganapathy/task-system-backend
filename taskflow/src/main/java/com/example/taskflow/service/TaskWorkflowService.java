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

    public List<Task> getTasksForUser(User user) {

        RoleStrategy strategy = roleStrategyFactory.getStrategy(user);

        // Director → Sees ALL tasks in the system
        if (strategy.canOverride(user)) {
            return taskRepository.findAll();
        }

        // Manager → Sees tasks assigned to TEAM + tasks assigned to SELF
        if (strategy.canAssign(user)) {
            // ✨ FIXED: Was previously only fetching team tasks
            return taskRepository.findByAssignedTo_IdOrAssignedTo_Manager_Id(user.getId(), user.getId());
        }

        // Employee → Only sees OWN tasks
        return taskRepository.findByAssignedTo_Id(user.getId());
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

    // Including the Reject fix from previous turn for completeness
    public Task rejectTask(Long taskId, User reviewer, String reason) {
        Task task = getTask(taskId);
        RoleStrategy strategy = roleStrategyFactory.getStrategy(reviewer);

        if (!strategy.canReview(reviewer, task)) {
            throw new RuntimeException("You are not authorized to reject this task");
        }

        task.setCurrentStatus("REJECTED");
        task.setReviewedBy(reviewer);
        Task updated = taskRepository.save(task);

        if (reason != null && !reason.trim().isEmpty()) {
            addComment(taskId, reviewer, "Rejection Reason: " + reason);
        }

        taskAuditService.recordStatus(updated, "REJECTED", reviewer);
        return updated;
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

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
        Task task = getTask(taskId); // Helper method you already have
        
        ChecklistItem item = new ChecklistItem();
        item.setTask(task);
        item.setText(text);
        item.setIsCompleted(false);
        
        return checklistItemRepository.save(item);
    }

    // ✨ NEW: TOGGLE CHECKLIST ITEM
    @Transactional
    public ChecklistItem toggleChecklistItem(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));
        
        item.setIsCompleted(!item.getIsCompleted());
        return checklistItemRepository.save(item);
    }
}