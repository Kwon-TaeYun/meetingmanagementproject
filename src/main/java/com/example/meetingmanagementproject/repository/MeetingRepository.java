package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m JOIN FETCH m.users")
    List<Meeting> findAllWithUsers();
}
