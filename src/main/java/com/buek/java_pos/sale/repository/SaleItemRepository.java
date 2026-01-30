package com.buek.java_pos.sale.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buek.java_pos.sale.entity.SaleItem;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {}