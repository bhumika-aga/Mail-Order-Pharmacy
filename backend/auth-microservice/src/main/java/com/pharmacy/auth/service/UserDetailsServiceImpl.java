package com.pharmacy.auth.service;

import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        
        return UserDetailsImpl.build(user);
    }
    
    /**
     * The account a token was issued to, if it still exists. Empty when the user
     * was deleted, or when the username now belongs to a newer account (the
     * token's "uid" claim names a different account). Tokens without a uid claim,
     * issued before it existed, are matched by username only.
     */
    @Transactional(readOnly = true)
    public Optional<UserDetailsImpl> loadTokenAccount(String username, Long tokenUserId) {
        return userRepository.findByUsername(username)
                   .filter(user -> tokenUserId == null || tokenUserId.equals(user.getId()))
                   .map(UserDetailsImpl::build);
    }
}