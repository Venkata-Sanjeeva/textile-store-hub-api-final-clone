package com.example.adminDashboardProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.adminDashboardProject.entity.Brand;
import com.example.adminDashboardProject.entity.Category;
import com.example.adminDashboardProject.repository.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all categories for the Homepage / Dropdowns
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Add a new category (Admin only)
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        // 1. Check if the brand name already exists
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: Category name '" + category.getName() + "' already exists.");
        }

        // 2. Save if it doesn't exist
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.created(null).body(savedCategory);
    }

    // Delete a category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
