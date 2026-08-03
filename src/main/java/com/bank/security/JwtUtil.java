package com.bank.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate Token
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract Email
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Extract Expiration
    public Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    // Validate Token
    public boolean isTokenValid(String token, String email) {

        String username = extractUsername(token);

        return username.equals(email) && !isTokenExpired(token);
    }

    // Check Expired
    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    // Extract Claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}