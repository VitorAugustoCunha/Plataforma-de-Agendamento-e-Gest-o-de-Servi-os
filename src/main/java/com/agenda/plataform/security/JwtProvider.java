package com.agenda.plataform.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {
    
    @Value("${app.jwt.secret:mySecretKeyForJWTTokenGenerationAndValidationPurposeOnlyAndMustBeAtLeast256BitsLongForHS512Algorithm}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}")
    private int jwtExpirationMs;
    
    private SecretKey signingKey;
    
    private SecretKey getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        return signingKey;
    }
    
    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims, userId);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    public String getEmailFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        Object email = claims.get("email");
        return email != null ? email.toString() : null;
    }
    
    public String getRoleFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        Object role = claims.get("role");
        return role != null ? role.toString() : null;
    }
    
    public java.time.Instant getExpiration(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null || claims.getExpiration() == null) {
            return null;
        }
        return claims.getExpiration().toInstant();
    }
    
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Token is null or empty");
            return false;
        }
        return isTokenValid(token);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Error extracting claims from token: {}", ex.getMessage());
        }
        return null;
    }
    
    private boolean isTokenValid(String token) {
        return getAllClaimsFromToken(token) != null;
    }
}