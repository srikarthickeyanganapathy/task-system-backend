package com.example.taskflow.dto;

public class TaskRequestDTO {

    private String title;
    private String description;
    private String assigneeUsername;
    private String creatorUsername;

    public TaskRequestDTO() {}

    public TaskRequestDTO(String title, String description,
                          String assigneeUsername, String creatorUsername) {
        this.title = title;
        this.description = description;
        this.assigneeUsername = assigneeUsername;
        this.creatorUsername = creatorUsername;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssigneeUsername() {
        return assigneeUsername;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssigneeUsername(String assigneeUsername) {
        this.assigneeUsername = assigneeUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
}
