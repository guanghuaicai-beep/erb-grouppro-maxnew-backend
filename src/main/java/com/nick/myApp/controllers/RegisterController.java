package com.nick.myApp.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.nick.myApp.dto.RegisterRequest;
import com.nick.myApp.models.Users;
import com.nick.myApp.repos.UsersRepo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor

public class RegisterController {
    
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request , BindingResult bindingResult) {
       
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        if(usersRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("email","Email already exists"));
        }

       if(usersRepo.existsByMobile(request.getMobile())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mobile","Mobile already exists"));
        }

        Users users = new Users();
        users.setFirstname(request.getFirstname());
        users.setLastname(request.getLastname());
        users.setUsername(request.getUsername());
        users.setEmail(request.getEmail());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setMobile(request.getMobile());
        //users.setCreatedAt(LocalDateTime.now());
    //users.setUpdatedAt(LocalDateTime.now());

    try {
        usersRepo.save(users);
    } catch (DataIntegrityViolationException e) {
        // catch database unique constraint error
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(Map.of("error", "Duplicate user detected"));
    }

    return ResponseEntity.ok(Map.of("message","Registration successful"));
    }

}
