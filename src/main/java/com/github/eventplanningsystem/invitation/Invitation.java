package com.github.eventplanningsystem.invitation;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private InvitationStatus invitationStatus;
}
