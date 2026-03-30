package com.nick.myApp.models;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email"),
      @UniqueConstraint(columnNames = "mobile") })
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "password" })

public class Users {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column(nullable = false, length = 100)
   private String firstname;

   @Column(nullable = false, length = 100)
   private String lastname;

   @Column(unique = false, nullable = false, length = 100)
   private String username;

   @Column(nullable = false, length = 255)
   private String email;

   @Column(nullable = false, length = 255)
   private String password;

   @Column(unique = true, nullable = false, length = 20)
   private String mobile;

}
