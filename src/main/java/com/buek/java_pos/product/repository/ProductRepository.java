package com.buek.java_pos.product.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.buek.java_pos.product.entity.Product;

public interface ProductRepository extends JpaRepository <Product, UUID>{
    boolean existsBySku(String sku);

    Page<Product> findByNameContainingIgnoreCase(String q, Pageable pageable);
}
