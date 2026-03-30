package com.nick.myApp.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // foreign key of users table

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course; // foreign key of courses table

    @Column(nullable = false)
    private Integer quantity = 1; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
