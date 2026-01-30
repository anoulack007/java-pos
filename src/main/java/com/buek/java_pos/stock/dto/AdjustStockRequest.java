package com.buek.java_pos.stock.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(
        @NotNull UUID productId,
        @NotNull Integer qtyChange) {

}
