package com.pharmacy.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.auth.dto.JwtResponse;
import com.pharmacy.auth.dto.LoginRequest;
import com.pharmacy.auth.dto.MessageResponse;
import com.pharmacy.auth.dto.SignupRequest;
import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.security.JwtUtils;
import com.pharmacy.auth.service.UserDetailsImpl;
import com.pharmacy.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails, userDetails.getMemberId());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                userDetails.getMemberId(),
                jwtUtils.getExpirationTime()));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate JWT token and return user info")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String memberId = jwtUtils.getMemberIdFromJwtToken(jwt);

                return ResponseEntity.ok(new JwtResponse(jwt, username, memberId, jwtUtils.getExpirationTime()));
            }
            return ResponseEntity.badRequest().body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User user = userService.createUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    signUpRequest.getFullName());

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Registration failed! " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get list of all registered users (Admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return userService.getUserById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody SignupRequest updateRequest) {
        try {
            User updatedUser = userService.updateUser(
                    id,
                    updateRequest.getUsername(),
                    updateRequest.getEmail(),
                    updateRequest.getMemberId(),
                    updateRequest.getFullName());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Update failed! " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete user by ID")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
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