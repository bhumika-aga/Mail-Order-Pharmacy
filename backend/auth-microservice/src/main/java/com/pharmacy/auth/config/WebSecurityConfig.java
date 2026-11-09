package com.pharmacy.auth.config;

import com.pharmacy.auth.security.AuthRateLimitFilter;
import com.pharmacy.auth.security.FixedWindowRateLimiter;
import com.pharmacy.auth.security.JwtAuthTokenFilter;
import com.pharmacy.auth.security.JwtUtils;
import com.pharmacy.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    
    /**
     * Browser origins allowed to call this API (comma-separated; env: APP_CORS_ALLOWED_ORIGINS).
     */
    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8085}")
    private List<String> allowedOrigins;
    
    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        return new JwtAuthTokenFilter(jwtUtils, userDetailsService);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Per-client-address limit on sign-in, sign-up and availability checks.
     */
    @Bean
    public AuthRateLimitFilter authRateLimitFilter(
        Clock clock, @Value("${app.auth.rate-limit.requests-per-minute:20}") int requestsPerMinute) {
        return new AuthRateLimitFilter(new FixedWindowRateLimiter(requestsPerMinute, Duration.ofMinutes(1), clock));
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthTokenFilter authenticationJwtTokenFilter,
                                           AuthRateLimitFilter authRateLimitFilter) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                                               // Public authentication endpoints
                                               .requestMatchers(HttpMethod.POST,
                                                   "/api/auth/signin", "/api/auth/signup", "/api/auth/validate").permitAll()
                                               .requestMatchers(HttpMethod.GET,
                                                   "/api/auth/check-username/**", "/api/auth/check-email/**").permitAll()
                                               // Docs, health and error rendering
                                               .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                                               .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                                               .requestMatchers("/error").permitAll()
                                               // H2 console exists only in the dev profile
                                               .requestMatchers("/h2-console/**").permitAll()
                                               .anyRequest().authenticated())
            // Missing or invalid credentials -> 401 (role failures stay 403)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        
        http.addFilterBefore(authRateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        // For H2 console
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Auth uses a bearer header, not cookies, so credentialed CORS is not needed.
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
