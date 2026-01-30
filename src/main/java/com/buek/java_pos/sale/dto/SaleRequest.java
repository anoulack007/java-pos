package com.buek.java_pos.sale.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleRequest(
    @NotNull List<Item> items
) {
    public record Item(
        @NotNull UUID productId,
        @Min(1) int qty
    ) {
    }

}
