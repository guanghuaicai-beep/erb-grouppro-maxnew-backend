package com.nick.myApp.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nick.myApp.models.Categories;
import com.nick.myApp.repos.CategoriesRepo;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor

public class CategoriesController {
    
    @Autowired
    private CategoriesRepo categoriesRepo;

    // view all categories
    @GetMapping
    public List<Categories> getAllCategories() {
        return categoriesRepo.findAll();
    }

    // view specific category
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoriesById(@PathVariable Integer id) {
        Optional<Categories> categories = categoriesRepo.findById(id);
        if (categories.isPresent()) {
        return ResponseEntity.ok(categories.get());
    } else {
        return ResponseEntity.status(401).body(Map.of("Error", "No Category Found"));
    }
    }
}
