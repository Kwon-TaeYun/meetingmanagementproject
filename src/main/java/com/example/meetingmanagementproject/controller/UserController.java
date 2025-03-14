package com.example.meetingmanagementproject.controller;

import com.example.meetingmanagementproject.dto.ErrorResponseDto;
import com.example.meetingmanagementproject.dto.LoginResponseDto;
import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> user) {
        return ResponseEntity.ok(userService.register(user.get("email"), user.get("password")));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, String> tokens = userService.login(email, password);
        if (tokens == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Authorization header is missing or invalid");
        }

        String accessToken = authorization.substring(7); // "Bearer " 제거
        userService.logout(accessToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}