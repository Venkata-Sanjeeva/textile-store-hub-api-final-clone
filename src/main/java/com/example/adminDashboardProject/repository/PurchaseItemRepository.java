package com.example.adminDashboardProject.repository;

import com.example.adminDashboardProject.entity.PurchaseItem;
import com.example.adminDashboardProject.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
    
    // Find all items belonging to a specific purchase using the entity relationship
    List<PurchaseItem> findByPurchase(Purchase purchase);

    // Find items by the Purchase String ID directly
    List<PurchaseItem> findByPurchase_PurchaseUniqueId(String uniqueId);
}
