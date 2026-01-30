package com.buek.java_pos.auth.dto;

import com.buek.java_pos.auth.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @Size(min = 6) String password,
        @NotNull Role role) {
}
