package com.example.adminDashboardProject.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.adminDashboardProject.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    List<Product> findByCategoryId(Long id);
    
    List<Product> findByBrandId(Long id);
    
    Optional<Product> findByUniqueId(String uniqueId);
    
    boolean existsByUniqueId(String uniqueId);
    
    // 1. Total Inventory Value (Base Price * Stock)
    @Query("SELECT SUM(p.basePrice * v.stockQuantity) FROM Product p JOIN p.variants v")
    BigDecimal getTotalInventoryValue();

    // 2. Total Units in Stock
    @Query("SELECT SUM(v.stockQuantity) FROM ProductVariant v")
    Long getTotalStockCount();

    // 3. Products out of stock (Where all variants are 0)
    @Query("SELECT COUNT(p) FROM Product p WHERE NOT EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.stockQuantity > 0)")
    Long countOutOfStockProducts();

    // 4. Products per Category (For Pie Chart)
    @Query("SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category.name")
    List<Object[]> getProductCountByCategory();
    
    // 5. Low stock alerts
    @Query("SELECT p.id, p.name, SUM(v.stockQuantity) as totalStock, COUNT(v) AS variantsCount " +
    	       "FROM Product p JOIN p.variants v " +
    	       "GROUP BY p.id, p.name " + // Ensure both are here
    	       "HAVING SUM(v.stockQuantity) < :threshold")
    List<Object[]> findLowStockProducts(@Param("threshold") Long threshold);
    
    // 6. Filtered Search (Native Query)
    @Query(value = "SELECT DISTINCT p.* FROM products p " +
            "LEFT JOIN product_variants v ON p.unique_id = v.product_unique_id " + // FIXED JOIN CONDITION
            "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:brandId IS NULL OR p.brand_id = :brandId) " +
            "AND (:size IS NULL OR :size = '' OR v.size = :size) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "(p.name ILIKE CONCAT('%', :search, '%') OR p.unique_id ILIKE CONCAT('%', :search, '%')))", 
            nativeQuery = true)
    List<Product> findFilteredProducts(
             @Param("categoryId") Long categoryId,
             @Param("brandId") Long brandId,
             @Param("size") String size,
             @Param("search") String search);
}