package com.example.meetingmanagementproject.repository;

import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleUserRepository extends JpaRepository<UserSchedule, Long> {
    Optional<UserSchedule> findByUserIdAndScheduleId(Long userId, Long scheduleId);
    @Query("SELECT us.user FROM UserSchedule us WHERE us.schedule.id = :scheduleId")
    List<User> findUsersByScheduleId(@Param("scheduleId") Long scheduleId);
}
