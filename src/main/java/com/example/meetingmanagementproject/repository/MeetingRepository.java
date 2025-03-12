package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
