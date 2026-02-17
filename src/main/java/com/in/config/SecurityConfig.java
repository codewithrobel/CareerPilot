package com.in.config;

import com.in.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // Swagger
                        .requestMatchers(
                                "/",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Auth APIs
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // ================= INTERVIEW APIs =================

                        // ADMIN can create/update/delete
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/interviews/**")
                        .hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/interviews/**")
                        .hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/interviews/**")
                        .hasRole("ADMIN")

                        // USER & ADMIN can view (important fix)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/interviews", "/interviews/**")
                        .hasAnyRole("USER", "ADMIN")

                        // ================= APPLICATION APIs =================

                        // USER can apply
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/applications/**")
                        .hasRole("USER")

                        // ADMIN can view all applications
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/applications/**")
                        .hasRole("ADMIN")

                        // ================= USER PROFILE =================
                        .requestMatchers("/users/**")
                        .hasAnyRole("USER", "ADMIN")

                        // ================= ADMIN APIs =================
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}