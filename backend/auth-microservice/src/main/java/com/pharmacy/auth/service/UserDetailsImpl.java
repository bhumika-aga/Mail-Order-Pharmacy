package com.pharmacy.auth.service;

import com.pharmacy.auth.entity.Role;
import com.pharmacy.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String memberId;
    private final Role role;
    
    public UserDetailsImpl(Long id, String username, String email, String password, String memberId, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.memberId = memberId;
        this.role = role;
    }
    
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getMemberId(),
            user.getRole());
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    public Long getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public Role getRole() {
        return role;
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
}
