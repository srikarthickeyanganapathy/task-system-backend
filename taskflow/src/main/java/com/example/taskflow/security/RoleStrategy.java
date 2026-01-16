package com.example.taskflow.security;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.User;

public interface RoleStrategy {

    boolean canAssign(User user);

    boolean canReview(User user, Task task);

    boolean canOverride(User user);
}
