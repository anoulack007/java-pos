package com.buek.java_pos.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        UUID categoryId
) {}