package com.nick.myApp.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.myApp.config.JwtUtil;
import com.nick.myApp.config.SendEmail;
import com.nick.myApp.dto.ForgetPasswordRequest;
import com.nick.myApp.models.Users;
import com.nick.myApp.repos.UsersRepo;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/forget_password")
public class ForgetPasswordController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UsersRepo usersRepo;
    private final JwtUtil jwtUtil; 
    private final SendEmail email;

    public ForgetPasswordController(
            UsersRepo usersRepo,
            JwtUtil jwtUtil, 
            SendEmail email) {
        this.usersRepo = usersRepo;
        this.jwtUtil = jwtUtil;
        this.email = email;
    }

    @PostMapping
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgetPasswordRequest request) {
        String identifier = request.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please provide email or mobile no"));
        }

        // 查找用戶（email 或 mobile）
        Optional<Users> userOpt = usersRepo.findByEmailIgnoreCase(identifier)
                .or(() -> usersRepo.findByMobile(identifier));

        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Reset password email has been sent"));
        }

        Users user = userOpt.get();

        String token = jwtUtil.generateResetToken(user);

        String resetLink = frontendUrl + "/reset_password?token=" + token;

        // send reset password email
        email.sendPasswordResetEmail(user.getEmail(), resetLink);


        return ResponseEntity.ok(Map.of("message", "Reset password email has been sent"));
    }
}