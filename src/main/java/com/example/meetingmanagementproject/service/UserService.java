package com.example.meetingmanagementproject.service;

import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.repository.UserRepository;
import com.example.meetingmanagementproject.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already exists";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            String token = jwtUtil.generateToken(user.get().getId());
            user.get().setToken(token);
            userRepository.save(user.get());
            return token;
        }
        return null;
    }

    public void logout(String token) {
        jwtUtil.invalidateToken(token);
    }

    public String getUserToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getToken).orElse(null);
    }
}
