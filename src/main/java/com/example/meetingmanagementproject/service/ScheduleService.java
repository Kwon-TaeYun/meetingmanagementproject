package com.example.meetingmanagementproject.service;

import com.example.meetingmanagementproject.dto.ScheduleDto;
import com.example.meetingmanagementproject.entity.Meeting;
import com.example.meetingmanagementproject.entity.Schedule;
import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.entity.UserSchedule;
import com.example.meetingmanagementproject.repository.ScheduleRepository;
import com.example.meetingmanagementproject.repository.ScheduleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleUserRepository scheduleUserRepository;
    @Transactional
    public Schedule saveSchedule(Schedule schedule){
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public List<ScheduleDto> getSchedulesByMeetingId(Long meetingId){
        List<Schedule> schedules = scheduleRepository.findByMeetingId(meetingId);
        return schedules.stream()
                .map(schedule -> new ScheduleDto(
                        schedule.getId(),
                        schedule.getTitle(),
                        schedule.getDate(),
                        schedule.getTime(),
                        schedule.getLocation()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSchedule joinSchedules(UserSchedule userSchedule){
        return scheduleUserRepository.save(userSchedule);
    }

    @Transactional
    public Schedule findSchedule(Long id){
        return scheduleRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void deleteUserSchedule(Long userId, Long scheduleId){
        UserSchedule userSchedule = scheduleUserRepository.findByUserIdAndScheduleId(userId, scheduleId).orElseThrow();
        scheduleUserRepository.delete(userSchedule);
    }

    @Transactional
    public List<User> findUsersFromSchedules(Long scheduleId){
        return scheduleUserRepository.findUsersByScheduleId(scheduleId);
    }
}
