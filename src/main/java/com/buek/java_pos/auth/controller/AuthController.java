package com.buek.java_pos.auth.controller;

import com.buek.java_pos.auth.dto.*;
import com.buek.java_pos.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest req){
        authService.register(req);
        return new RegisterResponse("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req){
        return authService.login(req);
    }
}
