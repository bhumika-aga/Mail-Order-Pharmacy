package com.pharmacy.auth.controller;

import com.pharmacy.auth.dto.*;
import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.security.JwtUtils;
import com.pharmacy.auth.service.UserDetailsImpl;
import com.pharmacy.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }
    
    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getRole().name();
        String jwt = jwtUtils.generateToken(userDetails.getUsername(), userDetails.getMemberId(), role);
        
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
        } catch (RuntimeException e) {
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
        description = "Update user information (own record, or any record for admins). Only admins can change a member ID.")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest updateRequest,
                                        @AuthenticationPrincipal UserDetailsImpl currentUser) {
        // A member re-pointing their own member ID could take over another member's
        // prescriptions and orders, so only admins may change it.
        String memberId = currentUser.isAdmin() ? updateRequest.getMemberId() : null;
        try {
            User updatedUser = userService.updateUser(
                id,
                updateRequest.getUsername(),
                updateRequest.getEmail(),
                memberId,
                updateRequest.getFullName());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete user", description = "Delete user by ID (Admin only)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
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
    
    @GetMapping("/check-memberid/{memberId}")
    @Operation(summary = "Check member ID availability", description = "Check if member ID is available")
    public ResponseEntity<?> checkMemberIdAvailability(@PathVariable String memberId) {
        boolean isAvailable = !userService.existsByMemberId(memberId);
        return ResponseEntity
                   .ok(new MessageResponse(isAvailable ? "Member ID is available" : "Member ID is already in use"));
    }
}
