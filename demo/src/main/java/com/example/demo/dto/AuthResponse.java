package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    @NotBlank
    private String accessToken;
    private String refreshToken;
}