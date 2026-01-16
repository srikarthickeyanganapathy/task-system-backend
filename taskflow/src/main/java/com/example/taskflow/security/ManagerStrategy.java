package com.example.taskflow.security;

import org.springframework.stereotype.Component;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;

@Component
public class ManagerStrategy implements RoleStrategy {

    @Override
    public boolean canAssign(User user) {
        return true;
    }

    @Override
    public boolean canReview(User user, Task task) {
        return task.getAssignedTo() != null
                && task.getAssignedTo().getManager() != null
                && task.getAssignedTo().getManager().getId().equals(user.getId());
    }

    @Override
    public boolean canOverride(User user) {
        return false;
    }
}
