package com.buek.java_pos.sale.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
    UUID id,
    String receiptNo,
    String cashierUsername,
    BigDecimal total,
    Instant createdAt,
    List<Item> items
) {
    public record Item(
        UUID productId,
        String sku,
        String name,
        int qty,
        BigDecimal unitPrice,
        BigDecimal lineTotal
    ) {
    }
}
