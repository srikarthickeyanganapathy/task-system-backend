package com.example.taskflow.security;

import org.springframework.stereotype.Component;

import com.example.taskflow.domain.User;

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
        String roleName = user.getRole().getName();

        return switch (roleName) {
            case "EMPLOYEE" -> employeeStrategy;
            case "MANAGER" -> managerStrategy;
            case "DIRECTOR" -> directorStrategy;
            default -> throw new RuntimeException("Unknown role: " + roleName);
        };
    }
}
