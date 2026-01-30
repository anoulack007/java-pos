package com.buek.java_pos.sale.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "sales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sale {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "receipt_no", nullable = false, unique = true)
    private String receiptNo;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal total;

    @Column(name = "cashier_username", nullable = false)
    private String cashierUsername;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

}
