package com.pharmacy.auth.service;

import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
    
    @Transactional
    public User createUser(String username, String email, String password, String fullName) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        // Auto-generate unique member ID
        String memberId = generateUniqueMemberId();
        
        // Create new user
        User user = new User(username, email, encoder.encode(password), memberId, fullName);
        return userRepository.save(user);
    }
    
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateUniqueMemberId() {
        String memberId;
        do {
            // Format: MEM + 6 digits (e.g. MEM001234). SecureRandom, so IDs can't be predicted.
            memberId = String.format("MEM%06d", RANDOM.nextInt(1, 1_000_000));
        } while (userRepository.existsByMemberId(memberId));

        return memberId;
    }

    /** Whether {@code rawPassword} is the current password of user {@code id}. */
    public boolean passwordMatches(Long id, String rawPassword) {
        return rawPassword != null && userRepository.findById(id)
                                          .map(user -> encoder.matches(rawPassword, user.getPassword()))
                                          .orElse(false);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByMemberId(String memberId) {
        return userRepository.findByMemberId(memberId);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByMemberId(String memberId) {
        return userRepository.existsByMemberId(memberId);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public User updateUser(Long id, String username, String email, String memberId, String fullName,
                           String newPassword) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if new username already exists (if changed)
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        
        // Check if new email already exists (if changed)
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        // Check if new member ID already exists (if changed) - only if memberId is
        // provided
        if (memberId != null && !user.getMemberId().equals(memberId) && userRepository.existsByMemberId(memberId)) {
            throw new RuntimeException("Error: Member ID is already in use!");
        }
        
        user.setUsername(username);
        user.setEmail(email);
        if (memberId != null) {
            user.setMemberId(memberId);
        }
        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (newPassword != null) {
            user.setPassword(encoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
}