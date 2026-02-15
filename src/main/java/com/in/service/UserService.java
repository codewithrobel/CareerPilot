package com.in.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.in.model.User;
import com.in.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.in.exception.EmailAlreadyExistsException;
import com.in.security.JwtUtil;
import com.in.dto.LoginResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Return data based on role (ADMIN -> all users, STUDENT -> only self)
    public Object getUsersBasedOnRole(String email) {

        // Find logged in user by email
        User loggedInUser = userRepository.findByEmail(email);

        if (loggedInUser == null) {
            throw new RuntimeException("User not found!");
        }

        // If ADMIN → return all users
        if ("ADMIN".equals(loggedInUser.getRole())) {
            return userRepository.findAll();
        }

        // If STUDENT → return only their own data
        return loggedInUser;
    }

    public User saveUser(User user) {

        // Duplicate email check
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // Generate access token
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Generate refresh token (7 days expiry)
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Save refresh token in database
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Return both tokens
        return new LoginResponse(accessToken, user.getEmail(), user.getRole());
    }

    // Register new user
    public String register(String name, String email, String password, String role) {

        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(password));

        user.setRole(role);

        userRepository.save(user);

        return "User Registered Successfully!";
    }
    // Get profile of logged-in user
    public User getProfile(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }
    // Update profile (only name)
    public User updateProfile(String email, String newName) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setName(newName);

        return userRepository.save(user);
    }
    public String changePassword(String email, String oldPassword, String newPassword) {

        // user find karo
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // old password match karo
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // new password encode karke save karo
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }

    // Refresh access token using refresh token
    public LoginResponse refresh(String refreshToken) {

        // Extract email from refresh token
        String email = jwtUtil.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate stored refresh token
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(newAccessToken, user.getEmail(), user.getRole());
    }
    // Logout user (invalidate refresh token)
    public void logout(String accessToken) {

        // Extract email from access token
        String email = jwtUtil.extractEmail(accessToken);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Remove refresh token from database
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}