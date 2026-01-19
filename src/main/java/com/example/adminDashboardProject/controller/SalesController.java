package com.example.adminDashboardProject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.adminDashboardProject.repository.SaleRepository;

@RestController
@RequestMapping("/api/admin/sales")
@CrossOrigin // Allows your React app to communicate with this backend 
public class SalesController {

    @Autowired
    private SaleRepository saleRepo;

    @GetMapping("/stats")
    public ResponseEntity<?> getSalesStats(@RequestParam String view) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> chartData = new ArrayList<>();

        if ("daily".equalsIgnoreCase(view)) {
            response.put("totalRevenue", saleRepo.getTotalRevenueForToday());
            // Format data for Recharts: { label: '10:00', value: 450 }
            saleRepo.getDailyChartData().forEach(row -> {
                Map<String, Object> point = new HashMap<>();
                point.put("label", row[0].toString() + ":00");
                point.put("value", row[1]);
                chartData.add(point);
            });
        } else {
            response.put("totalRevenue", saleRepo.getTotalRevenueForCurrentMonth());
            saleRepo.getMonthlyChartData().forEach(row -> {
                Map<String, Object> point = new HashMap<>();
                point.put("label", row[0].toString());
                point.put("value", row[1]);
                chartData.add(point);
            });
        }

        response.put("chartData", chartData);
        response.put("totalOrders", chartData.size()); // Sample logic for total orders
        return ResponseEntity.ok(response);
    }
}
