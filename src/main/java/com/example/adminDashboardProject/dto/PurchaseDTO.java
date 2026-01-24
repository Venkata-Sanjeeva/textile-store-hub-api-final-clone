package com.example.adminDashboardProject.dto;

import java.util.List;

import com.example.adminDashboardProject.entity.PurchaseItem;

import lombok.Data;

@Data
public class PurchaseDTO {

	private String purchaseUniqueId;
	private String customerName;
	private String customerPhone;
	private Double subtotal;
    private Double tax;
    private Double totalAmount;
    
    // Changed to a List of DTOs to avoid sending internal Entity logic to the frontend
    private List<PurchaseItem> items;
	
}

/*
 * 
{
  "purchaseUniqueId": "INV-2026-001",
  "customerName": "Rahul Sharma",
  "customerPhone": "9876543210",
  "subtotal": 1500.00,
  "tax": 270.00,
  "totalAmount": 1770.00,
  "items": [
    {
      "itemUniqueId": "ITM-001",
      "productName": "Slim Fit Shirt",
      "productVariant": "Blue / XL",
      "quantity": 2,
      "unitPriceAtPurchase": 500.00,
      "discountApplied": 10.0
    },
    {
      "itemUniqueId": "ITM-002",
      "productName": "Denim Jeans",
      "productVariant": "Black / 34",
      "quantity": 1,
      "unitPriceAtPurchase": 500.00,
      "discountApplied": 0.0
    }
  ]
}
 */
