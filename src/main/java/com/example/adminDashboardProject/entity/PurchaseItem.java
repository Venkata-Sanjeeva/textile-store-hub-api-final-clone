package com.example.adminDashboardProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "purchase_items")
@Data
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String itemUniqueId;

    @ManyToOne
    @JoinColumn(name = "purchase_unique_id", referencedColumnName = "purchaseUniqueId")
    private Purchase purchase;

    private String productName;
    private String productVariant;
    private Integer quantity;
    private Double unitPriceAtPurchase;
    private Double discountApplied;
}