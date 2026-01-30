package com.buek.java_pos.stock.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.buek.java_pos.product.entity.Product;
import com.buek.java_pos.product.repository.ProductRepository;
import com.buek.java_pos.stock.dto.AdjustStockRequest;
import com.buek.java_pos.stock.dto.StockResponse;
import com.buek.java_pos.stock.entity.Stock;
import com.buek.java_pos.stock.repository.StockRepository;

import jakarta.transaction.Transactional;

@Service
public class StockService {
    private final StockRepository stockRepo;
    private final ProductRepository productRepo;

    public StockService(StockRepository stockRepo, ProductRepository productRepo) {
        this.stockRepo = stockRepo;
        this.productRepo = productRepo;
    }

    public StockResponse get(UUID productId) {
        Stock s = stockRepo.findByProductId(productId)
                .orElseGet(() -> new Stock(null,
                        productRepo.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("product not found")),
                        0,
                        Instant.now()));

        return new StockResponse(productId, s.getQty());
    }

    @Transactional
    public StockResponse adjust(AdjustStockRequest req){
        Product p = productRepo.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        Stock s = stockRepo.findByProductId(req.productId())
                .orElseGet(() -> Stock.builder().product(p).qty(0).updatedAt(Instant.now()).build());

        int newQty = s.getQty() + req.qtyChange();

        if(newQty < 0 ) throw new IllegalAccessError("Stock cannot be negative");

        s.setQty(newQty);
        s.setUpdatedAt(Instant.now());
        stockRepo.save(s);

        return new StockResponse(p.getId(), s.getQty());
    }
}
