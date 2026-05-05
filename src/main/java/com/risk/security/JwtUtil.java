package com.risk.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Use a strong key (at least 256 bits for HS256)
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ⏱ Token validity (1 hour)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // ✅ Generate Token (with role)
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role) // IMPORTANT: keep as ADMIN / MANAGER / VIEWER
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // ✅ Extract Username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Extract Role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ✅ Validate Token (checks expiration + signature)
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Check expiration
            return !claims.getExpiration().before(new Date());

        } catch (Exception e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    // ✅ Extract all claims safely
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}