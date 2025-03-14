package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByMeetingId(Long meetingId);
}
