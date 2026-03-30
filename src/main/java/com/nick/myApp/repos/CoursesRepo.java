package com.nick.myApp.repos;

import com.nick.myApp.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoursesRepo extends JpaRepository<Courses, Integer> {
    
    Optional<Courses> findByCourseName(String coursName);
    List<Courses> findByCourseNameContaining(String keyword);
    
    List<Courses> findByCategory_Id(Integer categoryId);
    boolean existsByCourseName(String courseName);

  @Query("SELECT c FROM Courses c JOIN FETCH c.category cat " +
       "WHERE (:name IS NULL OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
       "AND (:category IS NULL OR LOWER(cat.category) LIKE LOWER(CONCAT('%', :category, '%'))) " +
       "AND (:startDate IS NULL OR c.startDate LIKE CONCAT('%', :startDate, '%')) " +
       "AND (:price IS NULL OR c.price = :price)")
List<Courses> searchCourses(@Param("name") String name,
                            @Param("category") String category,
                            @Param("startDate") String startDate,
                            @Param("price") Double price);

Courses findBySlug(String slug);








}
