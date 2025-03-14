package com.example.meetingmanagementproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_schedules")
@Getter @Setter
public class UserSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @Enumerated(EnumType.STRING)
    private ScheduleUserStatus status;
}
