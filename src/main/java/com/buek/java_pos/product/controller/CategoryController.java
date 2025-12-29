package com.buek.java_pos.product.controller;
import com.buek.java_pos.product.dto.*;
import com.buek.java_pos.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CategoryRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return service.list();
    }
}
