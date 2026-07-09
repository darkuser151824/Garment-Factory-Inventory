package com.example.demo.dto;

import com.example.demo.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}