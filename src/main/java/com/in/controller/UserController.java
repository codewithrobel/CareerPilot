package com.in.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.in.model.User;
import com.in.service.UserService;
import org.springframework.security.core.Authentication;
import com.in.dto.UpdateProfileRequest;
import org.springframework.http.ResponseEntity;
import com.in.dto.ChangePasswordRequest;
import jakarta.validation.Valid;

import java.util.List;

import com.in.service.StudentDashboardService;
import com.in.security.JwtUtil;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final StudentDashboardService studentDashboardService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public Object getUsers(Authentication authentication) {

        // JWT se logged-in user ka email milega
        String email = authentication.getName();

        return userService.getUsersBasedOnRole(email);
    }

    @GetMapping("/profile")
    public User getProfile(Authentication authentication) {

        // JWT se logged-in user ka email milega
        String email = authentication.getName();

        // Service ko email bhej rahe hain
        return userService.getProfile(email);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/profile")
    public User updateProfile(@RequestBody UpdateProfileRequest request,
                              Authentication authentication) {

        String email = authentication.getName();

        return userService.updateProfile(email, request.getName());
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        String message = userService.changePassword(
                email,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(message);
    }
    @GetMapping("/dashboard")
    public Map<String, Object> getStudentDashboard(
            @RequestHeader("Authorization") String header) {

        String token = header.substring(7); // remove Bearer

        String email = jwtUtil.extractEmail(token);

        return studentDashboardService.getStudentDashboard(email);
    }
}