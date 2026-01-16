package com.example.taskflow.dto;

public class TaskStatusUpdateDTO {

    private Long taskId;
    private String username;

    public TaskStatusUpdateDTO() {}

    public TaskStatusUpdateDTO(Long taskId, String username) {
        this.taskId = taskId;
        this.username = username;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getUsername() {
        return username;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
