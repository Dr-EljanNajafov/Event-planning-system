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
    private final UserRedisRepository userRedisRepository; // Redis repository


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
        // First, check Redis cache
        Optional<UserRedis> userRedisOptional = userRedisRepository.findByUsername(username);
        if (userRedisOptional.isPresent()) {
            UserRedis userRedis = userRedisOptional.get();
            return new UserDto(
                    userRedis.getId(),
                    userRedis.getUsername(),
                    userRedis.getRole()
            );
        }
        // Fallback to database if not found in cache
        UserE user = userService.user(username);
        // Cache the user in Redis
        cacheUserInRedis(user);
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
        // Cache the user in Redis
        cacheUserInRedis(user);
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

        user = userRepository.save(user);

        // Update cache
        cacheUserInRedis(user);

        return userInfo(user.getUsername());
    }

    private void cacheUserInRedis(UserE user) {
        UserRedis userRedis = new UserRedis();
        userRedis.setId(user.getId());
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        userRedisRepository.save(userRedis);
    }
}
