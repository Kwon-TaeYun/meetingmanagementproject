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
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        String email = user.get("email");
        String token = userService.login(email, user.get("password"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return token != null ? ResponseEntity.ok(new LoginResponseDto(token, email)) : ResponseEntity.status(401).body(new ErrorResponseDto("Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("Authorization Header: " + authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Authorization header is missing or invalid");
        }

        String token = authorization.substring(7); // "Bearer " 제거
        userService.logout(token);  // 서비스에서 로그아웃 처리
        return ResponseEntity.ok("Logged out successfully");
    }
}