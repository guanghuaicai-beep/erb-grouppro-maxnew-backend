package com.nick.myApp.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "courses")

public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Category cannot be empty")
    @Size(min = 3, max = 20, message = "Category must be between 3 and 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Category must be alphanumeric")
    private String category;



    // 一個 Category 可以有多個 Course
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonIgnore
    private List<Courses> courses;

}
