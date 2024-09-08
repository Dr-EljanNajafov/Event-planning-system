package com.github.eventplanningsystem.user;

import com.github.eventplanningsystem.auth.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    // Retrieve user info by ID
    public UserDto userInfo(long id) {
        UserE user = user(id);
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getInvitations() // Assuming invitations is a valid field in the User class
        );
    }

    // User sign-in (authentication)
    public AuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserE user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // User sign-up (registration)
    public AuthenticationResponse signUp(RegisterRequest request) {
        checkUsername(request.getUsername());

        UserE user = UserE.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // Update user information
    public AuthenticationResponse update(long id, UpdateRequest request) {
        checkUsername(request.getUsername(), id);

        UserE user = user(id);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return signIn(new AuthenticationRequest(request.getUsername(), request.getPassword()));
    }

    // Retrieve user by ID
    public UserE user(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with id %d doesn't exist".formatted(id)
                ));
    }

    // Check if username is already in use
    public void checkUsername(String username, long id) {
        Optional<UserE> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && !userOptional.get().getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }

    public void checkUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }
}
