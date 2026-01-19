package com.example.adminDashboardProject.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminDashboardProject.dto.BillingDTO;
import com.example.adminDashboardProject.dto.ProductVariantDTO;
import com.example.adminDashboardProject.entity.Product;
import com.example.adminDashboardProject.entity.ProductVariant;
import com.example.adminDashboardProject.entity.Sale;
import com.example.adminDashboardProject.repository.ProductRepository;
import com.example.adminDashboardProject.repository.ProductVariantRepository;
import com.example.adminDashboardProject.repository.SaleRepository;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository variantRepo;
    
    @Autowired
    private ProductRepository productRepo;
    
    @Autowired
    private SaleRepository saleRepo;
    
    private String generateVariantId() {
        return "VAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<ProductVariant> getAllVariantsByProductUniqueId(String productUniqueId) {
        return variantRepo.findByProduct_UniqueId(productUniqueId);
    }

    @Transactional
    public ProductVariant updateVariant(ProductVariant updateData) {
        ProductVariant existing = variantRepo.findById(updateData.getId())
            .orElseThrow(() -> new RuntimeException("Variant not found"));
        
        existing.setStockQuantity(updateData.getStockQuantity());
        // Add other fields if you want them to be editable (price, color, etc.)
        return variantRepo.save(existing);
    }

    @Transactional
    public void addVariantsToProduct(List<ProductVariantDTO> dtos) {
        for (ProductVariantDTO dto : dtos) {
            Product product = productRepo.findByUniqueId(dto.getProductUniqueId())
                .orElseThrow(() -> new RuntimeException("Parent Product not found: " + dto.getProductUniqueId()));
            
            ProductVariant variant = new ProductVariant();
            // Assign a unique ID if not provided in DTO
            variant.setVariantUniqueId(generateVariantId());
            variant.setColor(dto.getColor());
            variant.setSize(dto.getSize());
            variant.setAdditionalPrice(dto.getAdditionalPrice());
            variant.setStockQuantity(dto.getStockQuantity());
            variant.setProduct(product);
            
            variantRepo.save(variant);
        }
    }

//    @Transactional
//    public Boolean processBilling(List<BillingDTO> billingItems) {
//        for (BillingDTO item : billingItems) {
//            ProductVariant variant;
//            
//            // Try to find by Scan ID (SKU) first, then by DB ID
//            if (item.getVariantUniqueId() != null) {
//                variant = variantRepo.findByVariantUniqueId(item.getVariantUniqueId())
//                    .orElseThrow(() -> new RuntimeException("SKU " + item.getVariantUniqueId() + " not found"));
//            } else {
//                variant = variantRepo.findById(item.getVariantId())
//                    .orElseThrow(() -> new RuntimeException("Variant ID " + item.getVariantId() + " not found"));
//            }
//
//            // Check stock levels
//            if (variant.getStockQuantity() < item.getQuantity()) {
//                throw new RuntimeException("Insufficient stock for: " + variant.getVariantUniqueId());
//            }
//
//            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
//            variantRepo.save(variant);
//        }
//        return true;
//    }
    
 // Inside ProductVariantService.java

    @Transactional
    public Boolean updateListOfVariants(List<BillingDTO> billingItems) {
        for (BillingDTO item : billingItems) {
            ProductVariant variant;
            
            // 1. Find the variant
            if (item.getVariantUniqueId() != null && !item.getVariantUniqueId().isEmpty()) {
                variant = variantRepo.findByVariantUniqueId(item.getVariantUniqueId())
                    .orElseThrow(() -> new RuntimeException("SKU " + item.getVariantUniqueId() + " not found"));
            } else if (item.getVariantId() != null) {
                variant = variantRepo.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant ID " + item.getVariantId() + " not found"));
            } else {
                throw new RuntimeException("No identifier provided for billing item");
            }

            // 2. Check stock levels
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + variant.getColor() + " " + variant.getSize());
            }

            // 3. Deduct stock
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            variantRepo.save(variant);

            // 4. NEW: Record the Sale
            Sale sale = new Sale();
            sale.setVariantId(variant.getId());
            sale.setQuantity(item.getQuantity());
            // Assuming your ProductVariant has a price field
            sale.setTotalPrice(item.getTotalPrice());
         // Gets the exact time in India
            sale.setSaleDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
            saleRepo.save(sale);
        }
        return true;
    }

    @Transactional
    public boolean deleteVariant(Long variantId) {
        if (!variantRepo.existsById(variantId)) return false;
        variantRepo.deleteById(variantId);
        return true;
    }
}