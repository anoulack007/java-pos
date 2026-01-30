package com.buek.java_pos.stock.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buek.java_pos.stock.dto.AdjustStockRequest;
import com.buek.java_pos.stock.dto.StockResponse;
import com.buek.java_pos.stock.service.StockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @GetMapping("/{productId}")
    public StockResponse get(@PathVariable UUID productId) {
        return service.get(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/adjust")
    public StockResponse adjust(@Valid @RequestBody AdjustStockRequest req){
        return service.adjust(req);
    }
}
