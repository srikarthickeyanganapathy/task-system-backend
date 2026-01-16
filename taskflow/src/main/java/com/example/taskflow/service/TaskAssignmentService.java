package com.example.taskflow.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.security.RoleStrategy;
import com.example.taskflow.security.RoleStrategyFactory;

@Service
public class TaskAssignmentService {

    private final TaskRepository taskRepository;
    private final TaskAuditService taskAuditService;
    private final RoleStrategyFactory roleStrategyFactory;

    public TaskAssignmentService(TaskRepository taskRepository,
                                 TaskAuditService taskAuditService,
                                 RoleStrategyFactory roleStrategyFactory) {
        this.taskRepository = taskRepository;
        this.taskAuditService = taskAuditService;
        this.roleStrategyFactory = roleStrategyFactory;
    }

    public Task assignTask(String title, String description, User assignee, User creator, 
                           String priority, LocalDateTime dueDate, String tags) {

        RoleStrategy strategy = roleStrategyFactory.getStrategy(creator);

        if (!strategy.canAssign(creator)) {
            throw new RuntimeException("You are not allowed to assign tasks");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setAssignedTo(assignee);
        task.setCreatedBy(creator);
        
        // ✨ Set defaults if null
        task.setPriority(priority != null ? priority : "NORMAL");
        task.setDueDate(dueDate);
        task.setTags(tags);

        task.setCurrentStatus("ASSIGNED");
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        taskAuditService.recordStatus(savedTask, "ASSIGNED", creator);

        return savedTask;
    }
}