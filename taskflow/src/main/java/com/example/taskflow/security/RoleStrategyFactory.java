package com.example.taskflow.security;

import org.springframework.stereotype.Component;
import com.example.taskflow.domain.Role;
import com.example.taskflow.domain.User;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleStrategyFactory {

    private final EmployeeStrategy employeeStrategy;
    private final ManagerStrategy managerStrategy;
    private final DirectorStrategy directorStrategy;

    public RoleStrategyFactory(EmployeeStrategy employeeStrategy,
                               ManagerStrategy managerStrategy,
                               DirectorStrategy directorStrategy) {
        this.employeeStrategy = employeeStrategy;
        this.managerStrategy = managerStrategy;
        this.directorStrategy = directorStrategy;
    }

    public RoleStrategy getStrategy(User user) {
        // 1. Get all role names from the user's role set
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // 2. Check for roles in order of hierarchy (Highest -> Lowest)
        
        // If they are a DIRECTOR (or have ROLE_DIRECTOR)
        if (roleNames.contains("DIRECTOR") || roleNames.contains("ROLE_DIRECTOR")) {
            return directorStrategy;
        }

        // If they are a MANAGER
        if (roleNames.contains("MANAGER") || roleNames.contains("ROLE_MANAGER")) {
            return managerStrategy;
        }

        // Default to EMPLOYEE
        return employeeStrategy;
    }
}