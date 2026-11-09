package com.pharmacy.auth.controller;

import com.pharmacy.auth.client.MemberDataClient;
import com.pharmacy.auth.dto.*;
import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.security.JwtUtils;
import com.pharmacy.auth.security.LoginAttemptService;
import com.pharmacy.auth.service.UserAccountException;
import com.pharmacy.auth.service.UserDetailsImpl;
import com.pharmacy.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    /**
     * Only an admin, or the user themselves, may read or update a user record.
     */
    private static final String SELF_OR_ADMIN = "hasRole('ADMIN') or #id == authentication.principal.id";
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final LoginAttemptService loginAttempts;
    
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService,
                          LoginAttemptService loginAttempts) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.loginAttempts = loginAttempts;
    }
    
    @PostMapping("/signin")
    @Operation(summary = "User login",
        description = "Authenticate user and return JWT token. After 5 failed attempts for a username from the same address, sign-in is locked for 15 minutes (429).")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String attemptKey = LoginAttemptService.key(loginRequest.getUsername(), request.getRemoteAddr());
        Optional<Duration> locked = loginAttempts.lockedFor(attemptKey);
        if (locked.isPresent()) {
            long minutes = Math.max(1, locked.get().toMinutes() + 1);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                       .header(HttpHeaders.RETRY_AFTER, String.valueOf(locked.get().toSeconds() + 1))
                       .body(new MessageResponse("Too many failed sign-in attempts. Try again in " + minutes + " minutes."));
        }
        
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            loginAttempts.recordFailure(attemptKey);
            throw e; // -> 401 via the security entry point
        }
        loginAttempts.recordSuccess(attemptKey);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getRole().name();
        String jwt = jwtUtils.generateToken(userDetails.getUsername(), userDetails.getMemberId(), role,
            userDetails.getId());
        
        return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getUsername(),
            userDetails.getMemberId(),
            role,
            jwtUtils.getExpirationTime()));
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate JWT token and return user info")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtils.validateJwtToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid token"));
        }
        return ResponseEntity.ok(new JwtResponse(jwt,
            jwtUtils.getUserNameFromJwtToken(jwt),
            jwtUtils.getMemberIdFromJwtToken(jwt),
            jwtUtils.getRoleFromJwtToken(jwt),
            jwtUtils.getExpirationTime()));
    }
    
    @PostMapping("/signup")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            userService.createUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getFullName());
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (UserAccountException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all users", description = "Get list of all registered users (Admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/{id}")
    @PreAuthorize(SELF_OR_ADMIN)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (own record, or any record for admins)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                   .<ResponseEntity<?>>map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/users/{id}")
    @PreAuthorize(SELF_OR_ADMIN)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user",
        description = "Update user information (own record, or any record for admins). Only admins can change a member ID. "
                          + "To change the password send 'password'; members must also send their 'currentPassword', admins can reset it directly.")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest updateRequest,
                                        @AuthenticationPrincipal UserDetailsImpl currentUser) {
        // A member re-pointing their own member ID could take over another member's
        // prescriptions and orders, so only admins may change it.
        String memberId = currentUser.isAdmin() ? updateRequest.getMemberId() : null;
        
        String newPassword = updateRequest.getPassword();
        boolean changingPassword = newPassword != null && !newPassword.isBlank();
        // A stolen session token alone must not be enough to take over the account.
        if (changingPassword && !currentUser.isAdmin()
                && !userService.passwordMatches(id, updateRequest.getCurrentPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Current password is incorrect"));
        }
        try {
            User updatedUser = userService.updateUser(
                id,
                updateRequest.getUsername(),
                updateRequest.getEmail(),
                memberId,
                updateRequest.getFullName(),
                changingPassword ? newPassword : null);
            return ResponseEntity.ok(updatedUser);
        } catch (UserAccountException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete user",
        description = "Delete a user and permanently erase their prescriptions, subscriptions and refill orders (Admin only). "
                          + "Stock of orders that had not shipped is returned. If the data can't be erased right now, "
                          + "the user is kept (503) and the request can be retried.")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.deleteUser(id)) {
                return ResponseEntity.notFound().build();
            }
        } catch (MemberDataClient.MemberDataErasureException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                       .body(new MessageResponse("User was not deleted: their data in the " + e.getService()
                                                     + " service could not be erased right now. Please try again."));
        }
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }
    
    @GetMapping("/check-username/{username}")
    @Operation(summary = "Check username availability", description = "Check if username is available")
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = !userService.existsByUsername(username);
        return ResponseEntity
                   .ok(new MessageResponse(isAvailable ? "Username is available" : "Username is already taken"));
    }
    
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Check email availability", description = "Check if email is available")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable String email) {
        boolean isAvailable = !userService.existsByEmail(email);
        return ResponseEntity.ok(new MessageResponse(isAvailable ? "Email is available" : "Email is already in use"));
    }
}
