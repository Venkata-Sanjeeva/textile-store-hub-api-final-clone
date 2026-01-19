package com.example.adminDashboardProject.dto;

import lombok.Data;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingDTO {
    private Long variantId;         // Database PK
    private String variantUniqueId; // Business SKU (useful for barcode scanners)
    private Integer quantity;       // How many were bought
    private Long productId;     // Useful for front-end display
    private BigDecimal totalPrice;
}