package com.nick.myApp.repos;

import com.nick.myApp.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsRepo extends JpaRepository<Comments, Integer> {
    
    Optional<Comments> findByReviewName(String reviewName);
    List<Comments> findByCourseName(String courseName);
    List<Comments> findByCategory_Id(Integer categoryId);
}
