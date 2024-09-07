package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.invitation.Invitation;
import com.github.eventplanningsystem.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String location;

    private String description; // Описание мероприятия

    @ManyToOne
    private User owner; // Владелец мероприятия

    @OneToMany
    private List<Invitation> invitations;
}
