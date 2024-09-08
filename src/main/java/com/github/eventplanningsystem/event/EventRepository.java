package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.user.UserE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @NonNull
    Optional<Event> findById(@NonNull Long id);

}
