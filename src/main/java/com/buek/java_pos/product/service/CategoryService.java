package com.buek.java_pos.product.service;

import com.buek.java_pos.product.dto.*;
import com.buek.java_pos.product.entity.Category;
import com.buek.java_pos.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public CategoryResponse create(CategoryRequest req) {
        if (repo.existsByNameIgnoreCase(req.name())) {
            throw new IllegalArgumentException("category name already exists");
        }
        Category c = repo.save(Category.builder().name(req.name().trim()).build());
        return new CategoryResponse(c.getId(), c.getName());
    }

    public List<CategoryResponse> list() {
        return repo.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public Category getOrNull(UUID id) {
        if (id == null)
            return null;
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("category not found"));
    }
}
