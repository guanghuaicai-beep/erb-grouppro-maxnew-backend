package com.nick.myApp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "wishlist", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "course_id"})})
public class WishList {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

     @ManyToOne
     @JsonIgnoreProperties("wishList")
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

     @ManyToOne
          @JsonIgnoreProperties("wishList")

    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

}