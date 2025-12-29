package com.buek.java_pos.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        BigDecimal price,
        boolean active,
        UUID categoryId,
        String categoryName
) {}