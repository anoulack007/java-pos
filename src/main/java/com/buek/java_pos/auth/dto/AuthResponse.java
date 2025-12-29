package com.buek.java_pos.auth.dto;

import com.buek.java_pos.auth.entity.Role;

public record AuthResponse(
  String accessToken,
  Role role
) {}