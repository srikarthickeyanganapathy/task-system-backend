package com.example.taskflow.dto;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String assignedTo;
    private String createdBy;
    private String reviewedBy;
    private String currentStatus;

    public TaskResponseDTO() {}

    public TaskResponseDTO(Long id, String title, String description,
                           String assignedTo, String createdBy,
                           String reviewedBy, String currentStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.reviewedBy = reviewedBy;
        this.currentStatus = currentStatus;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
