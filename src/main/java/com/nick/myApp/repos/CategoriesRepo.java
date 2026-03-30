package com.nick.myApp.repos;

import com.nick.myApp.models.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepo extends JpaRepository<Categories, Integer> {
    
    Optional<Categories> findByCategory(String category);
    boolean existsByCategory(String category);
}
