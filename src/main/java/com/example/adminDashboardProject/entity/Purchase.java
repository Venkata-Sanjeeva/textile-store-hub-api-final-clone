package com.example.adminDashboardProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal database ID
    
    @Column(unique = true, nullable = false)
    private String purchaseUniqueId; 

    private String customerName;
    private String customerPhone;
    private Double subtotal;
    private Double tax;
    private Double totalAmount;
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<PurchaseItem> items;
}