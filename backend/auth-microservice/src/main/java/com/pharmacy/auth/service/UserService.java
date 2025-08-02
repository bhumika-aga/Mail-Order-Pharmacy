package com.pharmacy.auth.service;

import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
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
    
    private String generateUniqueMemberId() {
        String memberId;
        do {
            // Generate MemberID in format: MEM + 6 digits (e.g., MEM001234)
            int randomNum = (int) (Math.random() * 999999) + 1;
            memberId = String.format("MEM%06d", randomNum);
        } while (userRepository.existsByMemberId(memberId));
        
        return memberId;
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
    public User updateUser(Long id, String username, String email, String memberId, String fullName) {
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
        
        // Check if new member ID already exists (if changed) - only if memberId is provided
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
        
        return userRepository.save(user);
    }
}