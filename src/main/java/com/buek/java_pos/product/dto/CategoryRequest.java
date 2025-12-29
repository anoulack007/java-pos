package com.buek.java_pos.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(@NotBlank String name) {}