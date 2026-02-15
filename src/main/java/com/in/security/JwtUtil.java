

package com.in.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Static secret key (restart ke baad bhi same rahega)
    private static final String SECRET =
            "mySuperSecretKeymySuperSecretKeymySuperSecretKey";

    // HMAC key generate
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Generate JWT token (email + role dono store karenge)
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)              // email store
                .claim("role", role)            // 👈 role store kar rahe hain
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }
    // Extract all claims from token (used by JwtFilter)
    public Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Generate refresh token (long expiry)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .signWith(key)
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}