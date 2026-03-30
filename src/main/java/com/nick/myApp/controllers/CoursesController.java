package com.nick.myApp.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.nick.myApp.models.Courses;
import com.nick.myApp.repos.CoursesRepo;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor

public class CoursesController {

    @Autowired
    private CoursesRepo coursesRepo;

    // view all courses
    @GetMapping
    public List<Courses> getAllCourses() {
        return coursesRepo.findAll();
    }

    // view specific course
    @GetMapping("/{id}")
public ResponseEntity<?> getCourseById(@PathVariable Integer id) {
    Optional<Courses> course = coursesRepo.findById(id);
    if (course.isPresent()) {
        return ResponseEntity.ok(course.get());
    } else {
        return ResponseEntity.status(401).body(Map.of("Error", "No Course Found"));
    }
}

@GetMapping("/{slug}")
public ResponseEntity<Courses> getCourseBySlug(@PathVariable String slug) {
    Courses course = coursesRepo.findBySlug(slug);
    if (course == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(course);
}




    // search different courses by different conditions - Name , Category , Date , Price
   @GetMapping("/search")
public List<Courses> searchCourses(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) Double price) {
    return coursesRepo.searchCourses(name, category, startDate, price);
}

}