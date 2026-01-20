package com.example.adminDashboardProject.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.adminDashboardProject.dto.DashboardStatsDTO;
import com.example.adminDashboardProject.dto.ProductStockDTO;
import com.example.adminDashboardProject.entity.*;
import com.example.adminDashboardProject.repository.*;

@Service
public class ProductService {

    private final String UPLOAD_DIR = "/app/uploads";
    
    @Autowired
    private ProductRepository productRepo;
    
    @Autowired
    private BrandRepository brandRepo;
    
    @Autowired
    private CategoryRepository categoryRepo;
    
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    
    private String generateSecureUniqueId() {
        return "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Transactional
    public void saveProduct(String uniqueId, String name, String desc, Double price, Long bId, Long cId, MultipartFile file) throws IOException {
        
        // 1. Unique ID Logic
        String finalUniqueId;
        if (uniqueId == null || uniqueId.trim().isEmpty()) {
            finalUniqueId = generateSecureUniqueId();
        } else {
            if (productRepo.existsByUniqueId(uniqueId)) {
                throw new RuntimeException("Product ID " + uniqueId + " already exists!");
            }
            finalUniqueId = uniqueId;
        }

        // 2. Handle File Storage
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. Map to Entity
        Product product = new Product();
        product.setUniqueId(finalUniqueId);
        product.setName(name);
        product.setDescription(desc);
        product.setBasePrice(BigDecimal.valueOf(price));
        product.setImageUrl(fileName);
        
        // Fetch managed entities from DB to ensure referential integrity
        Category category = categoryRepo.findById(cId)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepo.findById(bId)
            .orElseThrow(() -> new RuntimeException("Brand not found"));
            
        product.setCategory(category);
        product.setBrand(brand);
        
        productRepo.save(product);
    }

    public Product getProductByUniqueId(String uniqueId) {
        return productRepo.findByUniqueId(uniqueId)
            .orElseThrow(() -> new RuntimeException("Product not found with UniqueID: " + uniqueId));
    }
    
    public void updateProductImage(Long id, MultipartFile file) throws IOException {
        // 1. Find the existing product
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 2. Define storage path
        Path uploadPath = Paths.get(UPLOAD_DIR);
        
        // 3. Delete the old image file if it exists
        String oldImageName = product.getImageUrl();
        if (oldImageName != null) {
            Path oldFilePath = uploadPath.resolve(oldImageName);
            try {
                Files.deleteIfExists(oldFilePath);
            } catch (IOException e) {
                // Log warning but continue; failing to delete an old file 
                // shouldn't necessarily stop the update
                System.err.println("Could not delete old file: " + e.getMessage());
            }
        }

        // 4. Save the new file
        String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            Path newFilePath = uploadPath.resolve(newFileName);
            Files.copy(inputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 5. Update the database record
        product.setImageUrl(newFileName);
        productRepo.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Delete image file
        if (product.getImageUrl() != null) {
            try {
                Path filePath = Paths.get(UPLOAD_DIR).resolve(product.getImageUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("File deletion failed: " + e.getMessage());
            }
        }
        productRepo.delete(product);
    }

    public DashboardStatsDTO getDashboardStats() {
        BigDecimal totalValue = productRepo.getTotalInventoryValue();
        Long totalStock = productRepo.getTotalStockCount();
        Long outOfStock = productRepo.countOutOfStockProducts();
        
        Map<String, Long> distribution = productRepo.getProductCountByCategory().stream()
            .collect(Collectors.toMap(
                array -> (String) array[0],
                array -> (Long) array[1],
                (existing, replacement) -> existing // Handle duplicate keys if any
            ));

        return new DashboardStatsDTO(
            totalValue != null ? totalValue : BigDecimal.ZERO,
            totalStock != null ? totalStock : 0L,
            outOfStock != null ? outOfStock : 0L,
            brandRepo.count(),
            categoryRepo.count(),
            distribution
        );
    }

    // ... other methods (getLowStockProducts, getFilteredProducts) remain same ...
    
    public List<ProductStockDTO> getLowStockProducts(int threshold) {
        List<Object[]> results = productRepo.findLowStockProducts((long) threshold);
        return results.stream()
            .map(result -> new ProductStockDTO((Long) result[0], (String) result[1], (Long) result[2], (Long) result[3]))
            .collect(Collectors.toList());
    }
    
    public List<Product> getFilteredProducts(Long categoryId, Long brandId, String size, String search) {
    	return productRepo.findFilteredProducts(categoryId, brandId, size, search);
    }
    
    public List<Product> getProductsByCategory(Long categoryId) {
    	List<Product> listOfProducts = productRepo.findByCategoryId(categoryId);
    	
    	return listOfProducts;
    }
    
    public List<Product> getProductsByBrand(Long brandId) {
    	List<Product> listOfProducts = productRepo.findByBrandId(brandId);
    	
    	return listOfProducts;
    }
    
}