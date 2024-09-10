package com.github.eventplanningsystem.admin.user;

import com.github.eventplanningsystem.admin.event.AdminEventService;
import com.github.eventplanningsystem.admin.report.AdminReportService;
import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public List<Long> users(GetUserRequest request) {
        if (request.getStart() < 0 || request.getCount() < 0)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' and 'count' must be > 0"
            );

        List<UserE> users = userRepository.findAll();
        return users
                .subList(Math.min(request.getStart(), users.size()), Math.min(request.getStart() + request.getCount(), users.size()))
                .stream()
                .map(UserE::getId)
                .toList();
    }

    public UserDto userInfo(String username) {
        return userService.userInfo(username);
    }

    public UserDto registerUser(RegisterByAdminRequest request) {
        userService.checkUsername(request.getUsername());

        UserE user = UserE
                .builder()
                .role(request.isAdmin() ? Role.ADMIN : Role.USER)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return userInfo(user.getUsername());
    }

    public UserDto updateUser(String username, UpdateByAdminRequest request) {
        userService.checkUsername(request.getUsername(), username);

        UserE user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Обновляем поля пользователя
        user.setRole(request.isAdmin() ? Role.ADMIN : Role.USER);
        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return userInfo(user.getUsername());
    }
}
