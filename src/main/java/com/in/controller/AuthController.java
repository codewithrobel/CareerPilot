package com.in.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.in.service.UserService;
import com.in.dto.LoginResponse;
import com.in.dto.LoginRequest;
import com.in.dto.RegisterRequest;
import jakarta.validation.Valid;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(
                request.getEmail(),
                request.getPassword()
        );
    }

    // Register new user
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request){
        return userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    // Refresh access token using refresh token
    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token is required");
        }

        return userService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = header.substring(7);

        userService.logout(token);

        return "Logged out successfully";
    }
}