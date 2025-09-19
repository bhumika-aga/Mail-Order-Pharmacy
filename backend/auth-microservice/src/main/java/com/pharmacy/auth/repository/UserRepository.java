package com.pharmacy.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmacy.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByMemberId(String memberId);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByMemberId(String memberId);
}