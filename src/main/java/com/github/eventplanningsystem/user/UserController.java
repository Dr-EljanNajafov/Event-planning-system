package com.github.eventplanningsystem.user;

import com.github.eventplanningsystem.auth.jwt.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;

    @Operation(summary = "Получение данных о текущем пользователе")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Me")
    public UserDto me(HttpServletRequest request) {
        return jwtService.accessUser(request, userService::userInfo);
    }

    @Operation(summary = "Получение нового jwt токена пользователя")
    @PostMapping("/SingIn")
    public AuthenticationResponse singIn(@RequestBody AuthenticationRequest request) {
        return userService.signIn(request);
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/SingUp")
    public AuthenticationResponse singUp(@RequestBody RegisterRequest request) {
        return userService.signUp(request);
    }

    @Operation(summary = "Обновление своего аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Update")
    public AuthenticationResponse update(HttpServletRequest request, @RequestBody UpdateRequest updateRequest) {
        return jwtService.accessUser(request, username -> userService.update(username, updateRequest));
    }
}
