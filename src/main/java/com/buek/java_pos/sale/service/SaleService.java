package com.buek.java_pos.sale.service;

import com.buek.java_pos.product.entity.Product;
import com.buek.java_pos.product.repository.ProductRepository;
import com.buek.java_pos.sale.dto.SaleRequest;
import com.buek.java_pos.sale.dto.SaleResponse;
import com.buek.java_pos.sale.entity.Sale;
import com.buek.java_pos.sale.entity.SaleItem;
import com.buek.java_pos.sale.repository.SaleRepository;
import com.buek.java_pos.stock.entity.Stock;
import com.buek.java_pos.stock.repository.StockRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.xml.crypto.Data;

@Service
public class SaleService {
    private final SaleRepository saleRepo;
    private final ProductRepository productRepo;
    private final StockRepository stockRepo;

    public SaleService(SaleRepository saleRepo, ProductRepository productRepo, StockRepository stockRepo) {
        this.saleRepo = saleRepo;
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
    }

    @Transactional
    public SaleResponse createSale(SaleRequest req) {
        String cashier = SecurityContextHolder.getContext().getAuthentication().getName();
        String receiptNo = genReceiptNo();

        // 1) preload products + validate
        Map<UUID, Product> products = new HashMap<>();
        for (SaleRequest.Item i : req.items()) {
            Product p = productRepo.findById(i.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product not found" + i.productId()));
            products.put(i.productId(), p);
        }

        // 2) check stock
        for (SaleRequest.Item i : req.items()) {
            Stock s = stockRepo.findByProductId(i.productId())
                    .orElseThrow(() -> new IllegalArgumentException("stock not found for product: " + i.productId()));
            if (s.getQty() < i.qty()) {
                throw new IllegalArgumentException("stock not enough for sku: " + products.get(i.productId()).getSku());
            }
        }

        // 3) build sale + items + total
        Sale sale = Sale.builder()
                .receiptNo(receiptNo)
                .cashierUsername(cashier)
                .createdAt(Instant.now())
                .total(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (SaleRequest.Item i : req.items()) {
            Product p = products.get(i.productId());

            BigDecimal unitPrice = p.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(i.qty()));

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(p)
                    .sku(p.getSku())
                    .name(p.getName())
                    .qty(i.qty())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();
            sale.getItems().add(item);
            total = total.add(lineTotal);

        }

        sale.setTotal(total);

        // 4) deduct stock
        for (SaleRequest.Item i : req.items()) {
            Stock s = stockRepo.findByProductId(i.productId()).orElseThrow();
            s.setQty(s.getQty() - i.qty());
            s.setUpdatedAt(Instant.now());
            stockRepo.save(s);
        }
        // 5) save sale
        sale = saleRepo.save(sale);

        return toResponse(sale);
    }

    private String genReceiptNo() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(java.time.LocalDateTime.now());
        String rand = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "POS-" + ts + "-" + rand;
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleResponse.Item> items = sale.getItems().stream()
                .map(it -> new SaleResponse.Item(
                        it.getProduct().getId(),
                        it.getSku(),
                        it.getName(),
                        it.getQty(),
                        it.getUnitPrice(),
                        it.getLineTotal()))
                .toList();

        return new SaleResponse(
                sale.getId(),
                sale.getReceiptNo(),
                sale.getCashierUsername(),
                sale.getTotal(),
                sale.getCreatedAt(),
                items);
    }

}
