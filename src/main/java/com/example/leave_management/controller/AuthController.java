package com.example.leave_management.controller;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.dto.AuthResponse;
import com.example.leave_management.dto.LoginRequest;
import com.example.leave_management.dto.RegisterRequest;
import com.example.leave_management.entity.User;
import com.example.leave_management.repository.UserRepository;
import com.example.leave_management.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (username.contains("@")) {
            return ResponseEntity.badRequest()
                    .body("Username cannot contain @");
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(request.getPassword()),
                User.Role.EMPLOYEE
        );

        userRepository.save(user);

        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        String usernameOrEmail = request.getUsername().trim();
        User user = userRepository.findByUsernameOrEmail(
                        usernameOrEmail,
                        usernameOrEmail
                )
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(401).build();
        }

        org.springframework.security.core.userdetails.User
                userDetails =
                (org.springframework.security.core.userdetails.User)
                        org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .authorities(
                                        "ROLE_" + user.getRole().name()
                                )
                                .build();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        user.getUsername(),
                        user.getRole().name()
                )
        );
    }
    
}