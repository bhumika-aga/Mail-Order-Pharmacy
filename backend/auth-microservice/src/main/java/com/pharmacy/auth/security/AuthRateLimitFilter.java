package com.pharmacy.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Per-client-address rate limit on the public auth endpoints (sign-in, sign-up
 * and the username/email availability checks). Slows down password guessing
 * and bulk account enumeration; over-limit requests get 429 with Retry-After.
 */
public class AuthRateLimitFilter extends OncePerRequestFilter {
    
    private final FixedWindowRateLimiter limiter;
    
    public AuthRateLimitFilter(FixedWindowRateLimiter limiter) {
        this.limiter = limiter;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.equals("/api/auth/signin")
                     || path.equals("/api/auth/signup")
                     || path.startsWith("/api/auth/check-"));
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String key = request.getRemoteAddr();
        if (limiter.tryAcquire(key)) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(limiter.secondsUntilReset(key)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Too many requests, please try again shortly\"}");
    }
}
