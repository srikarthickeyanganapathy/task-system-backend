package com.example.taskflow.controller;

import java.util.List;
import java.util.Map; // ✨ Add this

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskflow.domain.ChecklistItem; // ✨ Add this
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

    @GetMapping
    public List<Task> getTasks(@RequestParam String username) {
        User user = userService.getCurrentUser(username);
        return taskWorkflowService.getTasksForUser(user);
    }

    @PostMapping("/assign")
    public Task assignTask(@RequestBody TaskRequestDTO request) {
        User creator = userService.getCurrentUser(request.getCreatorUsername());
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
    public Task submitTask(@PathVariable Long taskId,
                           @RequestParam String username) {
        User user = userService.getCurrentUser(username);
        return taskWorkflowService.submitTask(taskId, user);
    }

    @PostMapping("/{taskId}/approve")
    public Task approveTask(@PathVariable Long taskId,
                            @RequestParam String username) {
        User reviewer = userService.getCurrentUser(username);
        return taskWorkflowService.approveTask(taskId, reviewer);
    }

    @PostMapping("/{taskId}/reject")
    public Task rejectTask(@PathVariable Long taskId,
                           @RequestParam String username,
                           @RequestBody(required = false) String reason) {
        User reviewer = userService.getCurrentUser(username);
        return taskWorkflowService.rejectTask(taskId, reviewer, reason);
    }

    @GetMapping("/{taskId}/comments")
    public List<TaskComment> getComments(@PathVariable Long taskId) {
        return taskWorkflowService.getComments(taskId);
    }

    @PostMapping("/{taskId}/comments")
    public TaskComment addComment(@PathVariable Long taskId,
                                  @RequestParam String username,
                                  @RequestBody String commentText) {
        User user = userService.getCurrentUser(username);
        return taskWorkflowService.addComment(taskId, user, commentText);
    }

    @PostMapping("/{taskId}/checklists")
    public ChecklistItem addChecklistItem(@PathVariable Long taskId, 
                                         @RequestBody Map<String, String> payload) {
        return taskWorkflowService.addChecklistItem(taskId, payload.get("text"));
    }

    @PostMapping("/checklists/{itemId}/toggle")
    public ChecklistItem toggleChecklistItem(@PathVariable Long itemId) {
        return taskWorkflowService.toggleChecklistItem(itemId);
    }
}