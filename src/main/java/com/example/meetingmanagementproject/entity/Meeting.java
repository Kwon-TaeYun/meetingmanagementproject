package com.example.meetingmanagementproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meetings")
@Getter @Setter
@NoArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "max_participants")
    private Integer maxParticipants;
    @Column(name = "current_participants")
    private Integer currentParticipants;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user; //meeting 생성 유저
    @ManyToMany
    @JoinTable(
            name = "users_meetings",
            joinColumns  = @JoinColumn(name="meeting_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    List<User> users = new ArrayList<>();
//    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Schedule> schedules;

    public Meeting(String name, String description, Integer maxParticipants) {
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 1;
    }
}
