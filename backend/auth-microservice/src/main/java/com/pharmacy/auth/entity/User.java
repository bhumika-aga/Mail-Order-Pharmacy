package com.pharmacy.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @NotBlank(message = "Username is required")
    private String username;
    
    @Column(unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @JsonIgnore
    @NotBlank(message = "Password is required")
    private String password;
    
    /*
     * Lower-cased username and email, kept unique by the database so names that
     * differ only in case can't both be registered, even by simultaneous requests
     * (see V3__case_insensitive_unique_keys.sql). Null only for case-variants that
     * already existed when the constraint was added.
     */
    @JsonIgnore
    @Column(name = "username_key")
    private String usernameKey;
    
    @JsonIgnore
    @Column(name = "email_key")
    private String emailKey;
    
    @Column(name = "member_id", unique = true)
    private String memberId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", columnDefinition = "VARCHAR(20)")
    private Role role = Role.MEMBER;
    
    @NotBlank(message = "Full name is required")
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public User() {
    }
    
    public User(String username, String email, String password, String memberId, String fullName) {
        this.username = username;
        this.usernameKey = key(username);
        this.email = email;
        this.emailKey = key(email);
        this.password = password;
        this.memberId = memberId;
        this.fullName = fullName;
    }
    
    private static String key(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }
    
    /**
     * Keeps the key in step with a new value. Changing only the case keeps the old
     * key, so a pre-existing case-variant (which has none) can still be edited.
     */
    private static String rekey(String oldValue, String newValue, String oldKey) {
        return newValue != null && newValue.equalsIgnoreCase(oldValue) ? oldKey : key(newValue);
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Rows created before the role column existed have a null role; they are
     * treated as ordinary members.
     */
    public Role getRole() {
        return role == null ? Role.MEMBER : role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.usernameKey = rekey(this.username, username, this.usernameKey);
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.emailKey = rekey(this.email, email, this.emailKey);
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}