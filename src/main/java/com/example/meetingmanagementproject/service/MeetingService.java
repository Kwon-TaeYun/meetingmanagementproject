package com.example.meetingmanagementproject.service;

import com.example.meetingmanagementproject.entity.Meeting;
import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    public List<Meeting> meetingList(){
        return meetingRepository.findAll();
    }

    @Transactional
    public Meeting saveMeeting(Meeting meeting, User user){
        meeting.getUsers().add(user);
        user.getMeetings().add(meeting);
        return meetingRepository.save(meeting);
    }
}
