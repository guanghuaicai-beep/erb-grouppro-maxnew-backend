package com.nick.myApp.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "student_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many-to-One 關聯到 Categories
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    @NotBlank(message = "Course name cannot be empty")
    @Size(max = 255, message = "Course name must be at most 255 characters")
    @Column(name = "course_name", nullable = false, length = 255)
    private String courseName;

    @NotBlank(message = "Review name cannot be empty")
    @Size(max = 50, message = "Review name must be at most 50 characters")
    @Column(name = "review_name", nullable = false, length = 50)
    private String reviewName;

    @NotNull(message = "Review date cannot be null")
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @NotBlank(message = "Review comment cannot be empty")
    @Column(name = "review_comment", nullable = false, columnDefinition = "TEXT")
    private String reviewComment;
}
