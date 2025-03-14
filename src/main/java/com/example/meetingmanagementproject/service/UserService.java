package com.example.meetingmanagementproject.service;

import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.repository.UserRepository;
import com.example.meetingmanagementproject.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Map<Long, String> refreshTokenStore = new ConcurrentHashMap<>();

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

    public Map<String, String> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && password.equals(userOptional.get().getPassword())) {
            User user = userOptional.get();
            Long userId = user.getId();

            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);
            // User 엔터티에 토큰 저장 후 DB 업데이트
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        }
        return null;
    }

    public void logout(String token) {
        Long userId = jwtUtil.validateToken(token);
        if (userId != null) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                log.info("accessToken :: " + user.getAccessToken());
                log.info("refreshToken :: " + user.getRefreshToken());
                user.setRefreshToken(null); // RT 삭제 (AT는 어차피 만료되므로 변경 X)
                userRepository.save(user);
                jwtUtil.invalidateToken(token);
            }
        }
    }

//    public String getUserToken(String email) {
//        Optional<User> user = userRepository.findByEmail(email);
//        return user.map(User::getToken).orElse(null);
//    }

    public User findByUserId(Long id){
        return userRepository.findById(id).orElseThrow();
    }
}
