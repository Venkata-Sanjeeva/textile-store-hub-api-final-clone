package com.example.adminDashboardProject.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminDashboardProject.dto.PurchaseDTO;
import com.example.adminDashboardProject.entity.Purchase;
import com.example.adminDashboardProject.entity.PurchaseItem;
import com.example.adminDashboardProject.repository.PurchaseRepository;

import jakarta.transaction.Transactional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    
    private String generatePurchaseId() {
        return "PUR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generatePurchaseItemId() {
    	return "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Purchase processPurchase(PurchaseDTO dto) {
        // 1. Create the Parent Entity
        Purchase purchase = new Purchase();
        
        // Generate a unique ID if not provided by frontend
        String uniqueId = generatePurchaseId();
        purchase.setPurchaseUniqueId(uniqueId);
        
        purchase.setCustomerName(dto.getCustomerName());
        purchase.setCustomerPhone(dto.getCustomerPhone());
        purchase.setSubtotal(dto.getSubtotal());
        purchase.setTax(dto.getTax());
        purchase.setTotalAmount(dto.getTotalAmount());

        // 2. Map Items and link them to the Parent
        List<PurchaseItem> items = dto.getItems().stream().map(itemDto -> {
            PurchaseItem item = new PurchaseItem();
            item.setItemUniqueId(generatePurchaseItemId());
            
            item.setProductName(itemDto.getProductName());
            item.setProductVariant(itemDto.getProductVariant());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPriceAtPurchase(itemDto.getUnitPriceAtPurchase());
            item.setDiscountApplied(itemDto.getDiscountApplied());
            
            // CRITICAL: Link item back to the purchase entity
            item.setPurchase(purchase);
            return item;
        }).collect(Collectors.toList());

        purchase.setItems(items);

        // 3. Save (CascadeType.ALL will handle the items automatically)
        return purchaseRepository.save(purchase);
    }
}
