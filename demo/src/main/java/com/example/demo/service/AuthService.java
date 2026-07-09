package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(userDetails);
        String refreshTOken= jwtUtil.generateRefreshToken(userDetails);
        return new AuthResponse(token,refreshTOken);
    }

    @Transactional
    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);
    }

    public AuthResponse refreshToken(String bearerToken) {
        String token = bearerToken.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (username != null && !jwtUtil.isTokenExpired(token)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateToken(userDetails);
            return new AuthResponse(newAccessToken, token);
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }
}
