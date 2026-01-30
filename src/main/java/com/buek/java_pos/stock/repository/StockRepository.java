package com.buek.java_pos.stock.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buek.java_pos.stock.entity.Stock;

public interface StockRepository extends JpaRepository<Stock,UUID> {
    Optional<Stock> findByProductId(UUID productId);
}
