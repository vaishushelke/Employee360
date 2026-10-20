
package com.employee360.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "RW1wbG95ZWUzNjBKd3RTZWNyZXRLZXlGb3JTcHJpbmdCb290UHJvamVjdDIwMjZTZWN1cmVLZXkhIQ==";

    private static final long TOKEN_EXPIRATION =
            1000L * 60 * 60; // 1 hour

    // GENERATE JWT TOKEN
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + TOKEN_EXPIRATION))
                .signWith(getSignKey())
                .compact();
    }

    // EXTRACT USERNAME
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    // VALIDATE TOKEN
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        Claims claims = extractAllClaims(token);

        return claims.getSubject() != null
                && claims.getSubject().equals(
                        userDetails.getUsername())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    // EXTRACT ALL CLAIMS
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // GET SIGNING KEY
    private SecretKey getSignKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}