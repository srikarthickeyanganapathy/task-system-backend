package com.example.taskflow.controller;

import com.example.taskflow.domain.User;
import com.example.taskflow.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Simple login endpoint (for development/testing)
    @GetMapping("/login")
    public User login(@RequestParam String username) {
        return userService.getCurrentUser(username);
    }
}
