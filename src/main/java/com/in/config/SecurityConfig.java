package com.in.config;

import com.in.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

                        // ✅ Swagger allow
                        .requestMatchers(
                                "/",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ✅ Public auth APIs
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // ✅ Interview APIs
                        // Only ADMIN can create/update/delete interviews
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/interviews/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/interviews/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/interviews/**").hasRole("ADMIN")

                        // USER & ADMIN can view interviews
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/interviews", "/interviews/**")
                        .hasAnyRole("USER", "ADMIN")

                        // ✅ Application APIs (only USER & ADMIN)
                        .requestMatchers("/applications/**").hasAnyRole("USER", "ADMIN")

                        // ✅ Admin only APIs
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ✅ User profile APIs
                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")

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