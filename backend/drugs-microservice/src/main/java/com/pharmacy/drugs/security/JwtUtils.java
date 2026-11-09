package com.pharmacy.drugs.security;

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
import java.util.function.Function;

@Component
public class JwtUtils {
    
    /**
     * HS512 needs a key of at least 512 bits. The secret comes only from the
     * JWT_SECRET environment variable and must match the auth service's.
     */
    static final int MIN_SECRET_BYTES = 64;
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    private final SecretKey signingKey;
    
    public JwtUtils(@Value("${jwt.secret}") String jwtSecret) {
        byte[] secretBytes = jwtSecret == null ? new byte[0] : jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("JWT_SECRET must be set and at least " + MIN_SECRET_BYTES
                                                + " bytes long (generate one with: openssl rand -base64 64)");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    }
    
    public String getUserNameFromJwtToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
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
}
