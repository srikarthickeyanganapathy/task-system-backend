package com.example.taskflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.taskflow.domain.User;
import com.example.taskflow.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getTeamMembers(Long managerId) {
        return userRepository.findByManager_Id(managerId);
    }
}
