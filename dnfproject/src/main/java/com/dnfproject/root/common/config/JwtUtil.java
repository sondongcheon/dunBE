package com.dnfproject.root.common.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String CATEGORY_ACCESS = "Authorization";
    private static final String CATEGORY_REFRESH = "Refresh";
    private static final String CLAIM_ADVENTURE_ID = "adventureId";
    private static final String CLAIM_ADVENTURE_NAME = "adventureName";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_CATEGORY = "category";

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${spring.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public Long getAdventureId(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get(CLAIM_ADVENTURE_ID, Long.class);
    }

    public String getAdventureName(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get(CLAIM_ADVENTURE_NAME, String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get(CLAIM_ROLE, String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get(CLAIM_CATEGORY, String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(Long adventureId, String adventureName, String role) {
        return Jwts.builder()
                .claim(CLAIM_CATEGORY, CATEGORY_ACCESS)
                .claim(CLAIM_ADVENTURE_ID, adventureId)
                .claim(CLAIM_ADVENTURE_NAME, adventureName)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long adventureId, String adventureName, String role) {
        return Jwts.builder()
                .claim(CLAIM_CATEGORY, CATEGORY_REFRESH)
                .claim(CLAIM_ADVENTURE_ID, adventureId)
                .claim(CLAIM_ADVENTURE_NAME, adventureName)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        return CATEGORY_REFRESH.equals(getCategory(token));
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public long getAccessExpirationSeconds() {
        return accessExpirationMs / 1000;
    }

    public long getRefreshExpirationSeconds() {
        return refreshExpirationMs / 1000;
    }

    public String resolveBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
}
