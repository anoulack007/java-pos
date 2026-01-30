package com.buek.java_pos.stock.dto;

import java.util.UUID;

public record StockResponse(
    UUID productId,
    int qty
) {

}
