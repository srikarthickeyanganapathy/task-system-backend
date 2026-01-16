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
        boolean isCreator = task.getCreatedBy() != null && 
                            task.getCreatedBy().getId().equals(user.getId());

        boolean isManagerOfAssignee = task.getAssignedTo() != null && 
                                      task.getAssignedTo().getManager() != null && 
                                      task.getAssignedTo().getManager().getId().equals(user.getId());
        return isCreator || isManagerOfAssignee;
    }

    @Override
    public boolean canOverride(User user) {
        return false;
    }
}
