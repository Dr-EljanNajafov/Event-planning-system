//package com.github.eventplanningsystem.admin.user;
//
//import com.github.eventplanningsystem.admin.event.AdminEventService;
//import com.github.eventplanningsystem.admin.report.AdminReportService;
//import com.github.eventplanningsystem.event.Event;
//import com.github.eventplanningsystem.user.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class AdminUserService {
//    private final UserRepository userRepository;
//    private final UserService userService;
//    private final AdminEventService eventService;
//    private final PasswordEncoder passwordEncoder;
//    private final AdminReportService reportService;
//
//    public List<Long> users(GetUserRequest request) {
//        if (request.getStart() < 0 || request.getCount() < 0)
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "'start' and 'count' must be > 0"
//            );
//
//        List<UserE> users = userRepository.findAll();
//        return users
//                .subList(Math.min(request.getStart(), users.size()), Math.min(request.getStart() + request.getCount(), users.size()))
//                .stream()
//                .map(UserE::getId)
//                .toList();
//    }
//
//    public UserDto userInfo(long id) {
//        UserE user = user(id);
//        return new UserDto(
//                user.getId(),
//                user.getUsername(),
//                user.getRole()
//        );
//    }
//
//    public UserE user(long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "User with id %d doesn't exist".formatted(id)
//                ));
//    }
//    public UserDto registerUser(RegisterByAdminRequest request) {
//        userService.checkUsername(request.getUsername());
//
//        UserE user = UserE
//                .builder()
//                .role(request.isAdmin() ? Role.ADMIN : Role.USER)
//                .username(request.getUsername())
//                .password(request.getPassword())
//                .build();
//        userRepository.save(user);
//        return userInfo(user.getId());
//    }
//
//    public UserDto updateUser(long id, UpdateByAdminRequest request) {
//        checkUsername(request.getUsername(), id);
//        UserE user = UserE
//                .builder()
//                .role(request.isAdmin() ? Role.ADMIN : Role.USER)
//                .username(request.getUsername())
//                .password(request.getPassword())
//                .build();
//        userRepository.save(user);
//        return userInfo(user.getId());
//    }
//
//    public void deleteUser(long id) {
//        UserE user = user(id);
//
//        userRepository.delete(user);
//    }
//
//    // Check if username is already in use
//    public void checkUsername(String username, long id) {
//        Optional<UserE> userOptional = userRepository.findByUsername(username);
//        if (userOptional.isPresent() && !userOptional.get().getId().equals(id)) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Username '%s' is already in use".formatted(username)
//            );
//        }
//    }
//
//    public void checkUsername(String username) {
//        if (userRepository.findByUsername(username).isPresent()) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Username '%s' is already in use".formatted(username)
//            );
//        }
//    }
//}
