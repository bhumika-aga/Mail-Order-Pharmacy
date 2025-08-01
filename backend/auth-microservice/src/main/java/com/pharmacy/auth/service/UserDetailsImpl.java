package com.pharmacy.auth.service;

import com.pharmacy.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private String memberId;
    
    public UserDetailsImpl(Long id, String username, String email, String password, String memberId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.memberId = memberId;
    }
    
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getMemberId());
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    
    public Long getId() { return id; }
    
    public String getEmail() { return email; }
    
    public String getMemberId() { return memberId; }
    
    @Override
    public String getPassword() { return password; }
    
    @Override
    public String getUsername() { return username; }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}