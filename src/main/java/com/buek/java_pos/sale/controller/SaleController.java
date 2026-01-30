package com.buek.java_pos.sale.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buek.java_pos.sale.dto.SaleRequest;
import com.buek.java_pos.sale.dto.SaleResponse;
import com.buek.java_pos.sale.service.SaleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PostMapping
    public SaleResponse create(@Valid @RequestBody SaleRequest req) {
        return service.createSale(req);
    } 

}
