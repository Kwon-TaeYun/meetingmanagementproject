package com.example.meetingmanagementproject.service;

import com.example.meetingmanagementproject.dto.JoinUserListDto;
import com.example.meetingmanagementproject.dto.MeetingDto;
import com.example.meetingmanagementproject.dto.MeetingListDto;
import com.example.meetingmanagementproject.entity.Meeting;
import com.example.meetingmanagementproject.entity.User;
import com.example.meetingmanagementproject.repository.MeetingRepository;
import com.example.meetingmanagementproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    public List<MeetingListDto> meetingList(){
        List<Meeting> meetings = meetingRepository.findAll();

        // Meeting을 MeetingListDto로 변환
        return meetings.stream()
                .map(meeting -> new MeetingListDto(
                        meeting.getId(),
                        meeting.getName(),
                        meeting.getDescription(),
                        meeting.getMaxParticipants(),
                        meeting.getCurrentParticipants()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Meeting findByMeetingId(Long id){
        return meetingRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Meeting saveMeeting(Meeting meeting){
       return meetingRepository.save(meeting);
    }

    @Transactional
    public void editMeeting(Long meetingId, MeetingDto meetingDto){
        Meeting meeting = findByMeetingId(meetingId);

        // 2. MeetingDto의 값을 이용해서 회의 정보 수정
        meeting.setName(meetingDto.getName());
        meeting.setDescription(meetingDto.getDescription());
        meeting.setMaxParticipants(meetingDto.getMaxParticipants());

        // 3. 수정된 meeting 객체를 저장 (자동으로 변경 사항이 반영됨)
        meetingRepository.save(meeting);
    }

    @Transactional
    public void deleteMeeting(Long meetingId){
        Meeting meeting = findByMeetingId(meetingId);
        meetingRepository.delete(meeting);
    }

    @Transactional
    public void joinMeeting(Meeting meeting, User user){
        meeting.getUsers().add(user);
//        user.getMeetings().add(meeting);
        meeting.setCurrentParticipants(meeting.getCurrentParticipants()+1);
    }

    @Transactional
    public List<JoinUserListDto> getParticipantList(Long id){
        Meeting meeting = meetingRepository.findById(id).orElseThrow();
        return meeting.getUsers().stream()
                .map(user -> new JoinUserListDto(
                        user.getId(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserAtMeeting(Long meetingId, Long userId){
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        meeting.setCurrentParticipants(meeting.getCurrentParticipants() - 1);
        // Meeting과 User 간의 관계를 끊는다
        meeting.getUsers().remove(user);

        // 변경 사항을 저장
        meetingRepository.save(meeting);
    }
}
