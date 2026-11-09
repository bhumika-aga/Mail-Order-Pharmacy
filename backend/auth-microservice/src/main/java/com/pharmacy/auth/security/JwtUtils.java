package com.pharmacy.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    
    /**
     * HS512 needs a key of at least 512 bits. The secret comes only from the
     * JWT_SECRET environment variable; there is deliberately no default.
     */
    static final int MIN_SECRET_BYTES = 64;
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    /**
     * Lifetime of an internal service token; one is minted per call, so keep it short.
     */
    private static final long SERVICE_TOKEN_TTL_MS = 60_000;
    
    private final SecretKey signingKey;
    
    private final long jwtExpirationMs;
    
    public JwtUtils(@Value("${jwt.secret}") String jwtSecret,
                    @Value("${jwt.expiration:900000}") long jwtExpirationMs) {
        byte[] secretBytes = jwtSecret == null ? new byte[0] : jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("JWT_SECRET must be set and at least " + MIN_SECRET_BYTES
                                                + " bytes long (generate one with: openssl rand -base64 64)");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }
    
    /**
     * A short-lived token with role SERVICE, for internal calls members must not be
     * able to make themselves (erasing a deleted member's data in other services).
     */
    public String generateServiceToken() {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                   .subject("auth-service")
                   .claim("role", "SERVICE")
                   .issuedAt(new Date(now))
                   .expiration(new Date(now + SERVICE_TOKEN_TTL_MS))
                   .signWith(signingKey, Jwts.SIG.HS512)
                   .compact();
    }
    
    /**
     * @param userId the account's database ID ("uid" claim): binds the token to that
     *               account, so it stops working if the user is deleted and the
     *               username is later registered again
     */
    public String generateToken(String username, String memberId, String role, Long userId) {
        long now = System.currentTimeMillis();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("uid", userId);
        if (memberId != null) {
            // Admin accounts have no member ID
            claims.put("memberId", memberId);
        }
        return Jwts.builder()
                   .claims(claims)
                   .subject(username)
                   .issuedAt(new Date(now))
                   .expiration(new Date(now + jwtExpirationMs))
                   .signWith(signingKey, Jwts.SIG.HS512)
                   .compact();
    }
    
    public String getUserNameFromJwtToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * The account ID the token was issued to, or null for tokens issued before the
     * claim existed.
     */
    public Long getUserIdFromJwtToken(String token) {
        Number uid = getClaimFromToken(token, claims -> claims.get("uid", Number.class));
        return uid == null ? null : uid.longValue();
    }
    
    public String getMemberIdFromJwtToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("memberId", String.class));
    }
    
    /**
     * Role claim; tokens issued before roles existed have none and count as MEMBER.
     */
    public String getRoleFromJwtToken(String token) {
        String role = getClaimFromToken(token, claims -> claims.get("role", String.class));
        return role == null ? "MEMBER" : role;
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                   .verifyWith(signingKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
    
    public Boolean validateJwtToken(String authToken) {
        try {
            getAllClaimsFromToken(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage()); // routine: the client signs in again
        } catch (JwtException e) {
            // A bad signature can mean a JWT_SECRET mismatch between services, or a forged token.
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    public Long getExpirationTime() {
        return jwtExpirationMs;
    }
}
