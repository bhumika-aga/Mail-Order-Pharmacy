package com.pharmacy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload for updating a user's profile. Unlike {@link SignupRequest} the password
 * is optional: omit it (or leave it blank) to keep the current one. Members
 * changing their own password must also send {@code currentPassword}.
 */
public class UpdateUserRequest {
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    private String currentPassword;

    public String getPassword() {
        // A blank field means "unchanged"
        return password == null || password.isBlank() ? null : password;
    }

    public void setPassword(String password) {
        this.password = password == null || password.isBlank() ? null : password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    private String memberId;
    
    public UpdateUserRequest() {
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
