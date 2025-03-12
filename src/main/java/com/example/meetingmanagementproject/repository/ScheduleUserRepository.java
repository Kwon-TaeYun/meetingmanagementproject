package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleUserRepository extends JpaRepository<UserSchedule, Long> {
}
