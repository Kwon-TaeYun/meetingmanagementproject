package com.example.meetingmanagementproject.controller;

import com.example.meetingmanagementproject.dto.JoinUserListDto;
import com.example.meetingmanagementproject.dto.MeetingDto;
import com.example.meetingmanagementproject.dto.MeetingListDto;
import com.example.meetingmanagementproject.dto.ScheduleDto;
import com.example.meetingmanagementproject.entity.*;
import com.example.meetingmanagementproject.service.MeetingService;
import com.example.meetingmanagementproject.service.ScheduleService;
import com.example.meetingmanagementproject.service.UserService;
import com.example.meetingmanagementproject.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ScheduleService scheduleService;
    @PostMapping
    public ResponseEntity<String> saveMeeting(@RequestHeader(value = "Authorization")String authorization, @RequestBody Meeting meeting){
        try{
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            log.info(authorization);
            log.info(token);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }

            // 유저 정보 가져오기
            User user = userService.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            meeting.setCurrentParticipants(0); // 예시: 참여자 수 1로 설정
            meeting.setUser(user);  // 유저 정보 설정

            // 저장 시 예외 발생 여부 확인
            meetingService.saveMeeting(meeting);
            meetingService.joinMeeting(meeting, user);
            return ResponseEntity.ok("저장 성공 !!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("저장 실패 !!");
        }
    }
    @GetMapping
    public ResponseEntity<List<MeetingListDto>> meetingList(){
        return ResponseEntity.ok(meetingService.meetingList());
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<String> editMeeting(@RequestHeader(value = "Authorization")String authorization, @RequestBody MeetingDto meetingDto, @PathVariable(name = "meetingId") Long meetingId){
        try{
            log.info(authorization);
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }
            Meeting meeting = meetingService.findByMeetingId(meetingId);

            if(userId != meeting.getUser().getId()){
                return ResponseEntity.status(401).body("수정할 권한이 없습니다.");
            }

            meetingService.editMeeting(meetingId,meetingDto); // 수정된 meeting 객체를 저장

            return ResponseEntity.ok("수정 성공 !!");
        }catch (Exception e){
            log.error("수정 실패! 예외 발생: ", e); // 예외를 로그로 출력
            return ResponseEntity.badRequest().body("수정 실패 !!");
        }
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@RequestHeader(value = "Authorization")String authorization, @PathVariable(name = "meetingId") Long meetingId){
        try{
            log.info(authorization);
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }
            Meeting meeting = meetingService.findByMeetingId(meetingId);

            if(userId != meeting.getUser().getId()){
                return ResponseEntity.status(401).body("삭제할 권한이 없습니다.");
            }
            meetingService.deleteMeeting(meetingId);
            return ResponseEntity.ok("삭제 성공 !!");


        }catch (Exception e){
            log.error("삭제 실패! 예외 발생: ", e); // 예외를 로그로 출력
            return ResponseEntity.status(401).body("삭제 실패 !!");
        }
    }

    @PostMapping("/{meetingId}/join")
    public ResponseEntity<String> joinMeeting(@RequestHeader(value = "Authorization")String authorization, @PathVariable(name = "meetingId") Long meetingId){
        try{
            log.info(authorization);
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }
            Meeting meeting = meetingService.findByMeetingId(meetingId);
            User user = userService.findByUserId(userId);
            if(meeting.getCurrentParticipants() == meeting.getMaxParticipants()){
                return ResponseEntity.status(401).body("정원 초과로 모임 모집이 마감되었습니다 !!");
            }

            meetingService.joinMeeting(meeting, user);
            return ResponseEntity.ok().body("참가 성공 !!");
        }catch (Exception e){
            log.error("참가 실패! 예외 발생: ", e); // 예외를 로그로 출력
            return ResponseEntity.status(401).body("참가 실패 !!");
        }
    }

    @DeleteMapping("/{meetingId}/leave")
    public ResponseEntity<String> leaveMeeting(@RequestHeader(value = "Authorization")String authorization,@PathVariable(name = "meetingId") Long meetingId){
        try{
            log.info(authorization);
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }
            Meeting meeting = meetingService.findByMeetingId(meetingId);
            List<ScheduleDto> schedules = scheduleService.getSchedulesByMeetingId(meetingId);
            User user = userService.findByUserId(userId);
            for (ScheduleDto schedule : schedules) {
                if(scheduleService.findUsersFromSchedules(schedule.getId()).contains(user)){
                    scheduleService.deleteUserSchedule(userId, schedule.getId());
                }
            } //user-schedule 테이블에서 해당 유저 기록 삭제
            if(meeting.getCurrentParticipants() == 1) {
                meetingService.deleteUserAtMeeting(meetingId, userId);
                meetingService.deleteMeeting(meetingId);
            }else{
                meetingService.deleteUserAtMeeting(meetingId, userId);
            }
            return ResponseEntity.ok().body("모임 탈퇴 성공 !!");
        }catch (Exception e){
            log.error("참가 실패! 예외 발생: ", e); // 예외를 로그로 출력
            return ResponseEntity.status(401).body("모임 탈퇴 실패 !!");
        }
    }

    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<List<JoinUserListDto>> meetingParticipantsList(@PathVariable(name = "meetingId") Long meetingId){
        return ResponseEntity.ok(meetingService.getParticipantList(meetingId));
    }

    @PostMapping("/{meetingId}/schedules")
    public ResponseEntity<String> createSchedules(@RequestHeader(value = "Authorization")String authorization, @PathVariable(name = "meetingId") Long meetingId, @RequestBody Schedule schedule){
        try{
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            log.info(authorization);
            log.info(token);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }

            // 유저 정보 가져오기
            User user = userService.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
//            schedule.setScheduleStatus(ScheduleStatus.ADMIN);
            Meeting meeting = meetingService.findByMeetingId(meetingId);
            schedule.setMeeting(meeting);
            scheduleService.saveSchedule(schedule);
            UserSchedule userSchedule = new UserSchedule();
            userSchedule.setUser(user);
            userSchedule.setSchedule(schedule);
            userSchedule.setStatus(ScheduleUserStatus.ATTENDING);
            scheduleService.joinSchedules(userSchedule);

            return ResponseEntity.ok("저장 성공 !!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("저장 실패 !!");
        }
    }

    @GetMapping("/{meetingId}/schedules")
    public ResponseEntity<List<ScheduleDto>> scheduleList(@PathVariable(name = "meetingId") Long meetingId){
        List<ScheduleDto> schedules = scheduleService.getSchedulesByMeetingId(meetingId);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/{meetingId}/schedules/{scheduleId}/join") //좀 더 수정 필요 !! meetingId에 속하는지 확인
    public ResponseEntity<String> joinSchedules(@RequestHeader(value = "Authorization")String authorization
            , @PathVariable(name = "meetingId") Long meetingId, @PathVariable(name = "scheduleId") Long scheduleId){
        try{
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            log.info(authorization);
            log.info(token);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }

            // 유저 정보 가져오기
            User user = userService.findByUserId(userId);
            Meeting meeting = meetingService.findByMeetingId(meetingId);
            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
            if(!meeting.getUsers().contains(user)){
                return ResponseEntity.status(401).body("미팅에 참여하고 있지 않아 스케줄에 참여하실 수 없습니다.");
            }
            Schedule schedule = scheduleService.findSchedule(scheduleId);

            UserSchedule userSchedule = new UserSchedule();
            userSchedule.setUser(user);
            userSchedule.setSchedule(schedule);
            userSchedule.setStatus(ScheduleUserStatus.ATTENDING);
            scheduleService.joinSchedules(userSchedule);


            return ResponseEntity.ok("일정 참여 성공 !!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("일정 참여 실패 !!");
        }
    }

    @DeleteMapping("/{meetingId}/schedules/{scheduleId}/leave")
    public ResponseEntity<String> leaveSchedule(@PathVariable(name = "meetingId")Long meetingId, @PathVariable(name = "scheduleId")Long scheduleId, @RequestHeader(value = "Authorization")String authorization){
        try{
            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("헤더에 정보가 없습니다!!");
            }
            String token = authorization.substring(7);
            log.info(authorization);
            log.info(token);
            Long userId = jwtUtil.validateToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("잘못된 토큰입니다.");
            }

            // 유저 정보 가져오기
            User user = userService.findByUserId(userId);
            Meeting meeting = meetingService.findByMeetingId(meetingId);
            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
            if(!meeting.getUsers().contains(user)){
                return ResponseEntity.status(401).body("미팅에 참여하고 있지 않아 스케줄에 참여하실 수 없습니다.");
            }

            Schedule schedule = scheduleService.findSchedule(scheduleId);

//            scheduleService.deleteUserSchedule(userId, scheduleId);
            scheduleService.updateUserScheduleStatus(userId, scheduleId, ScheduleUserStatus.NOT_ATTENDING);


            return ResponseEntity.ok("미참여로 상태 수정이 완료되었습니다 !!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("삭제 실패 !!");
        }
    }

    @GetMapping("/{meetingId}/schedules/{scheduleId}/participants")
    public List<JoinUserListDto> scheduleUserList(@PathVariable(name = "meetingId")Long meetingId, @PathVariable(name = "scheduleId")Long scheduleId){
        List<User> users = scheduleService.findUsersFromSchedules(scheduleId);
        return users.stream()
                .map(user -> new JoinUserListDto(
                        user.getId(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());
    }

}
