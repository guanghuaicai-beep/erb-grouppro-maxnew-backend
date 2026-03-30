package com.nick.myApp.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.myApp.config.JwtUtil;
import com.nick.myApp.dto.ResetPasswordRequest;
import com.nick.myApp.config.SendEmail;
import com.nick.myApp.models.Users;
import com.nick.myApp.repos.UsersRepo;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reset_password")
public class ResetPasswordController {
    
    private final UsersRepo usersRepo;
    private final JwtUtil jwtUtil; 
    private final PasswordEncoder passwordEncoder;
    private final SendEmail email;

    public ResetPasswordController (UsersRepo usersRepo, JwtUtil jwtUtil,PasswordEncoder passwordEncoder,SendEmail email) {
        this.usersRepo = usersRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
    }

    @PostMapping
public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

    if (request.getToken() == null || request.getToken().trim().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Missing reset token"));
    }

    if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "New password must be at least 8 characters"));
    }

    try {
        String email = jwtUtil.validateResetTokenAndGetEmail(request.getToken());

        Users user = usersRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepo.save(user);

        this.email.sendPasswordUpdatedNotification(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful. Please log in with your new password."
        ));

    } catch (IllegalArgumentException e) {
        System.out.println("Validation failed: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

    } catch (Exception e) {
        System.out.println("Unexpected error during reset: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Reset password failed", "details", e.getMessage()));
    }
}

    
}

    


    


