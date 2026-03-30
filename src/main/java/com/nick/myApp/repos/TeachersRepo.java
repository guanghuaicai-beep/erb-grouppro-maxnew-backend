package com.nick.myApp.repos;

import com.nick.myApp.models.Teachers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeachersRepo extends JpaRepository<Teachers, Integer> {
    
    Optional<Teachers> findByTeacherName(String teacherName);
    boolean existsByTeacherName(String teacherName);
}
