package edu.nipun.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") Long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expiration;
        log.info("JwtUtil initialized with signing key and expiration time: {}", expiration);
    }

    public String generateToken(UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            if (username == null || username.isEmpty()) {
                log.error("Cannot generate token for null or empty username");
                return null;
            }

            Map<String, Object> claims = new HashMap<>();

            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(signingKey)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage(), e);
        }
        return null;
    }

    public String extractUsername(String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.error("Cannot extract username from null or empty token");
                return null;
            }

            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || userDetails == null) {
            return false;
        }

        String username = extractUsername(token);
        if (username == null) {
            return false;
        }

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("Error checking token expiration: {}", e.getMessage(), e);
            return true;
        }
    }
}