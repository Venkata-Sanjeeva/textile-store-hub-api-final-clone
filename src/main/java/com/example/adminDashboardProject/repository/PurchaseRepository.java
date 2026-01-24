package com.example.adminDashboardProject.repository;

import com.example.adminDashboardProject.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    // Custom finder to fetch by your String Unique ID
    Optional<Purchase> findByPurchaseUniqueId(String purchaseUniqueId);

    // Useful for checking if an ID already exists before saving
    boolean existsByPurchaseUniqueId(String purchaseUniqueId);
}
