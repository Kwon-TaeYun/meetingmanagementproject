package com.example.meetingmanagementproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingListDto {
    Long id;
    String name;
    String description;
    Integer maxParticipants;
    Integer currentParticipants;
}
