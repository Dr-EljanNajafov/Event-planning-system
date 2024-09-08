package com.github.eventplanningsystem.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<UserE, UserDto> {
    @Override
    public UserDto apply(UserE user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getInvitations()
        );
    }
}
