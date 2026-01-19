package com.example.adminDashboardProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.adminDashboardProject.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Custom query to get revenue for today 
	@Query("SELECT SUM(s.totalPrice) FROM Sale s WHERE CAST(s.saleDate AS date) = CURRENT_DATE")
	Double getTotalRevenueForToday();

    // Custom query to get revenue for the current month 
	@Query("SELECT SUM(s.totalPrice) FROM Sale s " +
		       "WHERE EXTRACT(YEAR FROM s.saleDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
		       "AND EXTRACT(MONTH FROM s.saleDate) = EXTRACT(MONTH FROM CURRENT_DATE)")
	Double getTotalRevenueForCurrentMonth();

	@Query(value = "SELECT COUNT(*) FROM Sale s WHERE s.saleDate::date = CURRENT_DATE", nativeQuery = true)
    Long countSalesToday();

    // 4. Count Sales This Month
    @Query(value = "SELECT COUNT(*) FROM Sale s WHERE date_trunc('month', s.saleDate) = date_trunc('month', CURRENT_DATE)", nativeQuery = true)
    Long countSalesThisMonth();

	@Query("SELECT CAST(EXTRACT(HOUR FROM s.saleDate) AS int), SUM(s.totalPrice) FROM Sale s " +
		       "WHERE CAST(s.saleDate AS date) = CURRENT_DATE " +
		       "GROUP BY 1 ORDER BY 1 ASC")
	List<Object[]> getDailyChartData();

	@Query("SELECT CAST(s.saleDate AS date), SUM(s.totalPrice) FROM Sale s " +
		       "WHERE EXTRACT(MONTH FROM s.saleDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
		       "AND EXTRACT(YEAR FROM s.saleDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
		       "GROUP BY CAST(s.saleDate AS date) " +
		       "ORDER BY CAST(s.saleDate AS date) ASC")
	List<Object[]> getMonthlyChartData();
}
