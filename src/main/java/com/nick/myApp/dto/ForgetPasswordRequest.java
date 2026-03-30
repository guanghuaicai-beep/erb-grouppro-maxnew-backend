package com.nick.myApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ForgetPasswordRequest {
    
    @NotBlank(message = "Please provide email or mobile")
    @Pattern(
        regexp = "^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|\\+?\\d{8,15})$",
        message = "Please enter a valid email or mobile"
    )
    private String identifier;

    public String identifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
