package com.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // JwtUtil ko inject kar rahe hain
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Skip Swagger + OpenAPI endpoints completely
        if (path.contains("swagger") ||
            path.contains("api-docs") ||
            path.contains("webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Allow preflight CORS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header nikal rahe hain
        String authHeader = request.getHeader("Authorization");

        // Check karte hain header present hai aur Bearer se start ho raha hai
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // "Bearer " remove karke actual token nikalte hain
            String token = authHeader.substring(7);

            try {
                // Token validate karke claims extract karte hain
                Claims claims = jwtUtil.extractAllClaims(token);

                // Email subject se nikalte hain
                String email = claims.getSubject();

                // Role claim se nikalte hain
                String role = claims.get("role", String.class);

                // Spring Security authentication object bana rahe hain
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,                     // principal
                                null,                      // credentials
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + role) // authority
                                )
                        );

                // Security context me set kar rahe hain
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Agar token invalid hai to 401 return karo
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // Next filter ko call karo
        filterChain.doFilter(request, response);
    }
}