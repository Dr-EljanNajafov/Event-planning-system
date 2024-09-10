package com.github.eventplanningsystem.admin.user;

import com.github.eventplanningsystem.admin.AdminService;
import com.github.eventplanningsystem.user.UserDto;
import com.github.eventplanningsystem.user.UserE;
import com.github.eventplanningsystem.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class AdminUserController {
    private final AdminUserService service;
    private final AdminService adminService;
    private final UserRepository repository;

    @Operation(summary = "Получение списка всех пользователей")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public List<Long> users(GetUserRequest getUserRequest, HttpServletRequest request) {
        return adminService.checkAdmin(request, userId -> service.users(getUserRequest));
    }

    @Operation(summary = "Получение информации об пользователе по username")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public UserDto userInfo(HttpServletRequest request, @PathVariable long id) {
        return adminService.checkAdmin(request, service::userInfo);
    }

    @Operation(summary = "Создание администратором нового пользователя")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public UserDto registerUser(HttpServletRequest request, @RequestBody RegisterByAdminRequest registerByAdminRequest) {
        return adminService.checkAdmin(request, userId -> service.registerUser(registerByAdminRequest));
    }

    @Operation(summary = "Изменение администратором пользователя по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public UserDto updateUser(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateByAdminRequest updateByAdminRequest) {
        // Получаем пользователя по id
        UserE user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Извлекаем username
        String username = user.getUsername();

        // Проверяем, является ли текущий пользователь администратором
        return adminService.checkAdmin(request, userId -> service.updateUser(username, updateByAdminRequest));
    }
}
