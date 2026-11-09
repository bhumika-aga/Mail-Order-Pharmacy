package com.pharmacy.auth.service;

import com.pharmacy.auth.client.MemberDataClient;
import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String USERNAME_TAKEN = "Error: Username is already taken!";
    private static final String EMAIL_IN_USE = "Error: Email is already in use!";
    
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final MemberDataClient memberDataClient;
    
    public UserService(UserRepository userRepository, PasswordEncoder encoder, MemberDataClient memberDataClient) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.memberDataClient = memberDataClient;
    }
    
    /**
     * Emails are stored in lower case, so the database's unique constraint also
     * catches addresses that differ only in case.
     */
    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
    
    /**
     * Saves immediately, so a unique constraint violation surfaces here: two
     * requests can both pass the "is it taken?" checks before either has saved,
     * and the loser must get the same clean error as a normal duplicate.
     */
    private User saveChecked(User user) {
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAccountException("Error: Username or email is already taken!");
        }
    }
    
    @Transactional
    public User createUser(String username, String email, String password, String fullName) {
        email = normalizeEmail(email);
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new UserAccountException(USERNAME_TAKEN);
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAccountException(EMAIL_IN_USE);
        }
        
        // Auto-generate unique member ID
        String memberId = generateUniqueMemberId();
        
        return saveChecked(new User(username, email, encoder.encode(password), memberId, fullName));
    }
    
    private String generateUniqueMemberId() {
        String memberId;
        do {
            // Format: MEM + 6 digits (e.g. MEM001234). SecureRandom, so IDs can't be predicted.
            memberId = String.format("MEM%06d", RANDOM.nextInt(1, 1_000_000));
        } while (userRepository.existsByMemberId(memberId));
        
        return memberId;
    }
    
    /**
     * Whether {@code rawPassword} is the current password of user {@code id}.
     */
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
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(normalizeEmail(email));
    }
    
    /**
     * Deletes the user and erases all their pharmacy data in the other services
     * (prescriptions, subscriptions, refill orders). The data is erased first: if
     * that fails, the user is kept, so the deletion can simply be retried, and a
     * member ID is never freed for re-issue while records still point to it.
     * <p>
     * Not transactional: no database connection is held during the network calls.
     *
     * @return false if there is no such user
     * @throws MemberDataClient.MemberDataErasureException if the data could not be erased
     */
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return false;
        }
        String memberId = user.get().getMemberId();
        if (memberId != null) { // admin accounts may have no member data
            memberDataClient.eraseMemberData(memberId);
        }
        userRepository.deleteById(id);
        return true;
    }
    
    @Transactional
    public User updateUser(Long id, String username, String email, String memberId, String fullName,
                           String newPassword) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserAccountException("User not found with id: " + id));
        email = normalizeEmail(email);
        
        // Taken by another user? (Changing the case of one's own name is fine.)
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new UserAccountException(USERNAME_TAKEN);
        }
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new UserAccountException(EMAIL_IN_USE);
        }
        
        // Check if new member ID already exists (if changed) - only if memberId is
        // provided
        if (memberId != null && !memberId.equals(user.getMemberId()) && userRepository.existsByMemberId(memberId)) {
            throw new UserAccountException("Error: Member ID is already in use!");
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
        
        return saveChecked(user);
    }
}