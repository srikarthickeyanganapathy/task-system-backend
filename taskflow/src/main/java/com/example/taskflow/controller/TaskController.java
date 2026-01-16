package com.example.taskflow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskflow.domain.ChecklistItem;
import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskComment;
import com.example.taskflow.domain.User;
import com.example.taskflow.dto.TaskRequestDTO;
import com.example.taskflow.service.TaskAssignmentService;
import com.example.taskflow.service.TaskWorkflowService;
import com.example.taskflow.service.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskAssignmentService taskAssignmentService;
    private final TaskWorkflowService taskWorkflowService;
    private final UserService userService;

    public TaskController(TaskAssignmentService taskAssignmentService,
                          TaskWorkflowService taskWorkflowService,
                          UserService userService) {
        this.taskAssignmentService = taskAssignmentService;
        this.taskWorkflowService = taskWorkflowService;
        this.userService = userService;
    }

    // Helper to get currently logged-in user
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getCurrentUser(auth.getName());
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskWorkflowService.getTasksForUser(getCurrentUser());
    }

    @PostMapping("/assign")
    public Task assignTask(@RequestBody TaskRequestDTO request) {
        User creator = getCurrentUser();
        // The DTO contains the assignee username
        User assignee = userService.getCurrentUser(request.getAssigneeUsername());

        return taskAssignmentService.assignTask(
                request.getTitle(),
                request.getDescription(),
                assignee,
                creator,
                request.getPriority(),
                request.getDueDate(),
                request.getTags()
        );
    }

    @PostMapping("/{taskId}/submit")
    public Task submitTask(@PathVariable Long taskId) {
        return taskWorkflowService.submitTask(taskId, getCurrentUser());
    }

    @PostMapping("/{taskId}/approve")
    public Task approveTask(@PathVariable Long taskId) {
        return taskWorkflowService.approveTask(taskId, getCurrentUser());
    }

    @PostMapping("/{taskId}/reject")
    public Task rejectTask(@PathVariable Long taskId, @RequestBody(required = false) String reason) {
        return taskWorkflowService.rejectTask(taskId, getCurrentUser(), reason);
    }

    @GetMapping("/{taskId}/comments")
    public List<TaskComment> getComments(@PathVariable Long taskId) {
        return taskWorkflowService.getComments(taskId);
    }

    @PostMapping("/{taskId}/comments")
    public TaskComment addComment(@PathVariable Long taskId, @RequestBody String commentText) {
        return taskWorkflowService.addComment(taskId, getCurrentUser(), commentText);
    }

    @PostMapping("/{taskId}/checklists")
    public ChecklistItem addChecklistItem(@PathVariable Long taskId, @RequestBody Map<String, String> payload) {
        return taskWorkflowService.addChecklistItem(taskId, payload.get("text"));
    }

    @PostMapping("/checklists/{itemId}/toggle")
    public ChecklistItem toggleChecklistItem(@PathVariable Long itemId) {
        return taskWorkflowService.toggleChecklistItem(itemId);
    }
}