package com.pharmacy.auth.repository;

import com.pharmacy.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    // Usernames and emails are unique regardless of case ("Bob" and "bob" are the same user)
    boolean existsByUsernameIgnoreCase(String username);
    
    boolean existsByEmailIgnoreCase(String email);
    
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    
    Boolean existsByMemberId(String memberId);
}