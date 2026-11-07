package com.pharmacy.auth.entity;

/**
 * Account role. MEMBER is the default for every self-registered user; ADMIN can
 * manage all users and is granted directly in the database (never via the API).
 */
public enum Role {
    MEMBER,
    ADMIN
}
