package com.buek.java_pos.auth.service;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buek.java_pos.auth.dto.AuthResponse;
import com.buek.java_pos.auth.dto.LoginRequest;
import com.buek.java_pos.auth.dto.RegisterRequest;
import com.buek.java_pos.auth.entity.User;
import com.buek.java_pos.auth.repository.UserRepository;
import com.buek.java_pos.auth.security.JwtService;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository repo,
            PasswordEncoder encoder,
            AuthenticationManager authManager,
            JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest req) {
        logger.info("Registering user: {}", req.username());
        if (repo.existsByUsername(req.username())) {
            logger.warn("Registration failed: username already exists - {}", req.username());
            throw new IllegalArgumentException("username already exists");
        }

        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(req.role())
                .enabled(true)
                .build();

        repo.save(user);
        logger.info("User registered successfully: {}", req.username());
    }

    public AuthResponse login(LoginRequest req) {
        logger.info("Login attempt: {}", req.username());
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        User user = repo.findByUsername(req.username()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        logger.info("Login successful: {}", req.username());
        return new AuthResponse(token, user.getRole());
    }
}
