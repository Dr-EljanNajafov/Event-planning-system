package com.github.eventplanningsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserE, Long> {
    Optional<UserE> findByUsername(String string);
}
