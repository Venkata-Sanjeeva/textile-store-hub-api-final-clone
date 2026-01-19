package com.example.adminDashboardProject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long variantId;
    private Integer quantity;
    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;
    private LocalDateTime saleDate; // This is crucial for "Daily/Monthly" filters
}