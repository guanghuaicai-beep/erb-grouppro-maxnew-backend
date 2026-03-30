package com.nick.myApp.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.nick.myApp.dto.WishListRequest;
import com.nick.myApp.models.*;
import com.nick.myApp.repos.CoursesRepo;
import com.nick.myApp.repos.UsersRepo;
import com.nick.myApp.repos.WishListRepo;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/wishlist")

// 🔥🔥🔥 這行已修正（支援 ngrok + localhost）
@CrossOrigin(originPatterns = "*")

public class WishListController {

    @Autowired
    private WishListRepo wishListRepo;

    @Autowired
    private CoursesRepo coursesRepo;

    @Autowired
    private UsersRepo usersRepo;

    private Users getUser(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Unauthorized - No user logged in");
        }

        String identifier = authentication.getName();

        if (identifier.contains("@")) {
            return usersRepo.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        } else {
            return usersRepo.findByMobile(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        }
    }

    // ✅ 查看 wishlist
    @GetMapping
    public ResponseEntity<List<WishList>> getWishlist(Authentication authentication) {
        try {
            Users user = getUser(authentication);
            return ResponseEntity.ok(wishListRepo.findByUser_Id(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Collections.emptyList());
        }
    }

    // ✅ 比較課程
    @GetMapping("/compare")
    public ResponseEntity<List<Courses>> compareCourses(
            @RequestParam(name = "courseId1") Integer courseId1,
            @RequestParam(name = "courseId2") Integer courseId2,
            Authentication authentication) {

        try {
            if (courseId1 == null || courseId2 == null || courseId1.equals(courseId2)) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            Users user = getUser(authentication);
            Integer userId = user.getId();

            boolean hasCourse1 = wishListRepo.existsByUser_IdAndCourse_Id(userId, courseId1);
            boolean hasCourse2 = wishListRepo.existsByUser_IdAndCourse_Id(userId, courseId2);

            if (!hasCourse1 || !hasCourse2) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            Optional<Courses> optCourse1 = coursesRepo.findById(courseId1);
            Optional<Courses> optCourse2 = coursesRepo.findById(courseId2);

            if (optCourse1.isEmpty() || optCourse2.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            return ResponseEntity.ok(List.of(optCourse1.get(), optCourse2.get()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Collections.emptyList());
        }
    }

    // ✅ 加入收藏
    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(@RequestBody WishListRequest request,
            Authentication authentication) {
        try {
            Users user = getUser(authentication);
            Integer courseId = request.getCourseId();

            if (wishListRepo.findByUser_IdAndCourse_Id(user.getId(), courseId).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Course already exists in wishlist");
            }

            Courses course = coursesRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            WishList wish = new WishList();
            wish.setUser(user);
            wish.setCourse(course);
            wishListRepo.save(wish);

            return ResponseEntity.ok(course);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    // ✅ 移除收藏
    @DeleteMapping("/{courseId}")
    @Transactional
    public ResponseEntity<String> removeFromWishlist(@PathVariable Integer courseId,
            Authentication authentication) {
        try {
            Users user = getUser(authentication);

            boolean exists = wishListRepo.existsByUser_IdAndCourse_Id(user.getId(), courseId);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Course not found in wishlist");
            }

            wishListRepo.deleteByUser_IdAndCourse_Id(user.getId(), courseId);
            return ResponseEntity.ok("Course has been removed from wishlist");

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}