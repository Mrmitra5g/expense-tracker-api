package com.example.expensetracker.service;

import com.example.expensetracker.entity.User;
import com.example.expensetracker.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String rawPassword) {
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role("ROLE_USER")
                .build();
        return userRepo.save(u);
    }

    public User loadByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public User findByUsernameOrThrow(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
