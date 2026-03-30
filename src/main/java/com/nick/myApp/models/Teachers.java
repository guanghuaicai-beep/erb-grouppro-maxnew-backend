package com.nick.myApp.models;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "teachers")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Teachers {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Length(min = 3 , max = 60 , message = "Course name must be between 3 and 60 characters")
   @NotBlank(message = "Course name cannot be empty")
   @Column(nullable = false , length = 60)
   private String courseName;

   @Length(min = 3 , max = 16 , message = "Teacher name must be between 3 and 16 characters")
   @NotBlank(message = "Teacher name cannot be empty")
   @Column(nullable = false , length = 16)
   private String teacherName;

   @Length(min = 3 , max = 8 , message = "Greeting must be between 3 and 8 characters")
   @NotBlank(message = "Greeting cannot be empty")
   @Column(nullable = false , length = 8)
   private String greeting;

   @Length(min = 3 , max = 19 , message = "Firstname must be between 3 and 19 characters")
   @NotBlank(message = "Firstname cannot be empty")
   @Column(nullable = false , length = 19)
   private String teacherFirstname;

   @Length(min = 3 , max = 18 , message = "Lastname must be between 3 and 18 characters")
   @NotBlank(message = "Last name cannot be empty")
   @Column(nullable = false , length = 18)
   private String teacherLastname;

   @Length(min = 3 , max = 38 , message = "Title must be between 3 and 38 characters")
   @NotBlank(message = "Title cannot be empty")
   @Column(nullable = false , length = 38)
   private String teacherTitle;

   @Length(min = 10 , max = 79 , message = "Qualification must be between 10 and 79 characters")
   @NotBlank(message = "Qualification cannot be empty")
   @Column(nullable = false , length = 79)
   private String teacherQualification;

   @Length(min = 10 , max = 905 , message = "Biography must be between 10 and 905 characters")
   @NotBlank(message = "Biography cannot be empty")
   @Column(nullable = false , length = 905)
   private String teacherBiography;
}
