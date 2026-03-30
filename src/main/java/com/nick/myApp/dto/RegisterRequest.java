package com.nick.myApp.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterRequest {
   @NotBlank(message = "First name cannot be empty")
    @Length(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with uppercase letter")
    private String firstname;

    @NotBlank(message = "Last name cannot be empty")
    @Length(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with uppercase letter")
    private String lastname;

    @NotBlank(message = "Last name cannot be empty")
    @Length(min = 3, max = 255)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "Only numbers allowed")
    @Size(min = 8, max = 20)
    private String mobile;
}
