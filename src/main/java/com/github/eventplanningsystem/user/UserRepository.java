package com.github.eventplanningsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserE, Long> {
    Optional<UserE> findByUsername(String string);
}
