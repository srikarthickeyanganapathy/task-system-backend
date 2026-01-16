package com.example.taskflow.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskStatusHistory;
import com.example.taskflow.domain.User;
import com.example.taskflow.repository.TaskStatusHistoryRepository;

@Service
public class TaskAuditService {

    private final TaskStatusHistoryRepository historyRepository;

    public TaskAuditService(TaskStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void recordStatus(Task task, String status, User user) {
        TaskStatusHistory history = new TaskStatusHistory();
        history.setTask(task);
        history.setStatus(status);
        history.setChangedBy(user);
        history.setChangedAt(LocalDateTime.now());

        historyRepository.save(history);
    }
}
