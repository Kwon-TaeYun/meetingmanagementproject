package com.example.meetingmanagementproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    Long id;
    String title;
    LocalDate date;
    LocalTime time;
    String location;
}
