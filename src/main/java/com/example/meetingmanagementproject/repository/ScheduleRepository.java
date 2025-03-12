package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
