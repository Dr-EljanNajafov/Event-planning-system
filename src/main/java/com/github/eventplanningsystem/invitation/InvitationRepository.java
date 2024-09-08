package com.github.eventplanningsystem.invitation;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.user.UserE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findAllByUser(UserE currentUser);
    void deleteAllByEvent(Event event);

}
