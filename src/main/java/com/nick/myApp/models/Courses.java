package com.nick.myApp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Course name cannot be empty")
    @Size(max = 60, message = "Course name must be at most 60 characters")
    @Column(name = "course_name", nullable = false, length = 60)
    private String courseName;

    @NotBlank(message = "Description cannot be empty")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Study mode cannot be null")
    @Column(name = "study_mode", nullable = false, length = 12)
    private String studyMode;

    @NotBlank(message = "Price cannot be empty")
    @Size(max = 10, message = "Price must be at most 10 characters")
    @Column(name = "price", nullable = false, length = 10)
    private Double price;

    @NotNull(message = "Level cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 14)
    private CourseLevel level;

    @NotBlank(message = "Duration cannot be empty")
    @Size(max = 10, message = "Duration must be at most 10 characters")
    @Column(name = "duration", nullable = false, length = 10)
    private String duration;

    @NotBlank(message = "Capacity cannot be empty")
    @Size(max = 10, message = "Capacity must be at most 10 characters")
    @Column(name = "capacity", nullable = false, length = 10)
    private String capacity;

     @NotBlank(message = "Slug cannot be empty")
    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @NotBlank(message = "Start date cannot be empty")
    @Size(max = 12, message = "Start date must be at most 12 characters")
    @Column(name = "start_date", nullable = false, length = 12)
    private String startDate;

    @NotBlank(message = "Time cannot be empty")
    @Size(max = 22, message = "Time must be at most 22 characters")
    @Column(name = "time", nullable = false, length = 22)
    private String time;

    @NotNull(message = "Status cannot be null")
    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Categories category;

    @Transient
     public String getCategoryName() {
      return category != null ? category.getCategory() : null;
}

}
