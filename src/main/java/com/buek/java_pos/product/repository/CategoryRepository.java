package com.buek.java_pos.product.repository;

import java.util.Optional;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;

import com.buek.java_pos.product.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,UUID> {
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
