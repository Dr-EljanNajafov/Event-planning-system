package com.github.eventplanningsystem.admin;

import com.github.eventplanningsystem.auth.jwt.JWTService;
import com.github.eventplanningsystem.user.Role;
import com.github.eventplanningsystem.user.UserE;
import com.github.eventplanningsystem.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JWTService jwtService;
    private final UserRepository userRepository;


    // Версия без возврата результата (void)
    public void checkAdminVoid(HttpServletRequest request, Consumer<String> adminConsumer) {
        checkAdmin(request, username -> {
            adminConsumer.accept(username);
            return null;  // Так как это версия без возврата значения
        });
    }

    // Версия с возвратом результата
    public <T> T checkAdmin(HttpServletRequest request, Function<String, T> adminConsumer) {
        // Используем accessUser для извлечения имени пользователя (username)
        return jwtService.accessUser(request, username -> {
            // Находим пользователя по username
            UserE user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Проверяем роль пользователя
            if (user.getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
            }

            // Если проверка прошла, применяем переданную функцию adminConsumer
            return adminConsumer.apply(username);
        });
    }
}
