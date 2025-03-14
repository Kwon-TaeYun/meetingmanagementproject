package com.example.meetingmanagementproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDto {
    private String name;
    private String description;
    private Integer maxParticipants;
}
