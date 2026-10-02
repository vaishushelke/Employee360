package com.employee360.backend.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // 58-byte secret = strong enough for HS384
    private static final String SECRET_KEY =
            "RW1wbG95ZWUzNjBKd3RTZWNyZXRLZXlGb3JTcHJpbmdCb290UHJvamVjdDIwMjZTZWN1cmVLZXkhIQ==";


    // ==========================================
    // GENERATE TOKEN
    // ==========================================

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()

                .subject(userDetails.getUsername())

                .issuedAt(new Date())

                .expiration(
                    new Date(
                        System.currentTimeMillis()
                        + 1000 * 60 * 60
                    )
                )

                .signWith(
                    getSignKey(),
                    SignatureAlgorithm.HS384
                )

                .compact();
    }


    // ==========================================
    // EXTRACT USERNAME
    // ==========================================

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }


    // ==========================================
    // VALIDATE TOKEN
    // ==========================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }


    // ==========================================
    // CHECK TOKEN EXPIRATION
    // ==========================================

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }


    // ==========================================
    // EXTRACT CLAIMS
    // ==========================================

    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                .verifyWith(getSignKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    // ==========================================
    // SECRET KEY
    // ==========================================

    private SecretKey getSignKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}