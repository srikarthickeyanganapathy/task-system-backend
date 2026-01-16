package com.example.taskflow.security;

import org.springframework.stereotype.Component;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;

@Component
public class DirectorStrategy implements RoleStrategy {

    @Override
    public boolean canAssign(User user) {
        return true;
    }

    @Override
    public boolean canReview(User user, Task task) {
        return true;
    }

    @Override
    public boolean canOverride(User user) {
        return true;
    }
}
