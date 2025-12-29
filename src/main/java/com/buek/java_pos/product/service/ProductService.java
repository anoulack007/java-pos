package com.buek.java_pos.product.service;

import com.buek.java_pos.product.dto.*;
import com.buek.java_pos.product.entity.Category;
import com.buek.java_pos.product.entity.Product;
import com.buek.java_pos.product.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ProductService {

    private final ProductRepository repo;
    private final CategoryService categoryService;

    public ProductService(ProductRepository repo, CategoryService categoryService) {
        this.repo = repo;
        this.categoryService = categoryService;
    }

    public ProductResponse create(ProductRequest req) {
        if (repo.existsBySku(req.sku())) {
            throw new IllegalArgumentException("sku already exists");
        }

        Category cat = categoryService.getOrNull(req.categoryId());

        Product p = Product.builder()
                .sku(req.sku().trim())
                .name(req.name().trim())
                .price(req.price())
                .active(true)
                .category(cat)
                .build();

        p = repo.save(p);
        return toResponse(p);
    }

    public ProductResponse get(UUID id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
    }

    public Page<ProductResponse> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> result = (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNameContainingIgnoreCase(q, pageable);

        return result.map(this::toResponse);
    }

    public ProductResponse update(UUID id, ProductRequest req) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("product not found"));

        if (!p.getSku().equals(req.sku()) && repo.existsBySku(req.sku())) {
            throw new IllegalArgumentException("sku already exists");
        }

        Category cat = categoryService.getOrNull(req.categoryId());

        p.setSku(req.sku().trim());
        p.setName(req.name().trim());
        p.setPrice(req.price());
        p.setCategory(cat);

        return toResponse(repo.save(p));
    }

    public void deactivate(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("product not found"));
        p.setActive(false);
        repo.save(p);
    }

    private ProductResponse toResponse(Product p) {
        UUID catId = p.getCategory() != null ? p.getCategory().getId() : null;
        String catName = p.getCategory() != null ? p.getCategory().getName() : null;

        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getPrice(),
                Boolean.TRUE.equals(p.getActive()),
                catId,
                catName
        );
    }
}
