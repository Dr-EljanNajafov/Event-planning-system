//package com.github.eventplanningsystem.admin.user;
//
//import com.github.eventplanningsystem.admin.AdminService;
//import com.github.eventplanningsystem.user.UserDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/admin/user")
//public class AdminUserController {
//    private final AdminUserService service;
//    private final AdminService adminService;
//
//    @Operation(summary = "Получение списка всех пользователей")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @GetMapping
//    public List<Long> accounts(GetUserRequest getUserRequest, HttpServletRequest request) {
//        return adminService.checkAdmin(request, userId -> service.users(getUserRequest));
//    }
//
//    @Operation(summary = "Получение информации об пользователе по id")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @GetMapping("/{id}")
//    public UserDto userInfo(HttpServletRequest request, @PathVariable int id) {
//        return adminService.checkAdmin(request, userId -> service.userInfo(id));
//    }
//
//    @Operation(summary = "Создание администратором нового пользователя")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @PostMapping
//    public UserDto registerUser(HttpServletRequest request, @RequestBody RegisterByAdminRequest registerByAdminRequest) {
//        return adminService.checkAdmin(request, userId -> service.registerUser(registerByAdminRequest));
//    }
//
//    @Operation(summary = "Изменение администратором пользователя по id")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @PutMapping("/{id}")
//    public UserDto updateUser(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateByAdminRequest updateByAdminRequest) {
//        return adminService.checkAdmin(request, userId -> service.updateUser(id, updateByAdminRequest));
//    }
//
//    @Operation(summary = "Удаление пользователя по id")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @DeleteMapping("/{id}")
//    public void deleteUser(HttpServletRequest request, @PathVariable long id) {
//        adminService.checkAdminVoid(request, userId -> service.deleteUser(id));
//    }
//}
