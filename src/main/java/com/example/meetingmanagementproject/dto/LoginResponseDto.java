package com.example.meetingmanagementproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder //중간에서 값을 가로채게 할 수 없게...
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String email;

}
