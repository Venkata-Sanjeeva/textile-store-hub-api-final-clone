package com.example.adminDashboardProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.adminDashboardProject.dto.PurchaseDTO;
import com.example.adminDashboardProject.entity.Purchase;
import com.example.adminDashboardProject.service.PurchaseService;

@RestController
@RequestMapping("/api/admin/purchases")
@CrossOrigin // Adjust for your frontend URL
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/finalize")
    public ResponseEntity<Purchase> finalizePurchase(@RequestBody PurchaseDTO purchaseDTO) {
        Purchase savedPurchase = purchaseService.processPurchase(purchaseDTO);
        return new ResponseEntity<>(savedPurchase, HttpStatus.CREATED);
    }
}