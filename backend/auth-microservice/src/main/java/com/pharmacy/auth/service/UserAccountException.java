package com.pharmacy.auth.service;

/**
 * A user account request that can't be carried out as asked (e.g. the username
 * is taken). Its message is meant for the client; any other exception is an
 * unexpected error and is not shown to clients.
 */
public class UserAccountException extends RuntimeException {
    
    public UserAccountException(String message) {
        super(message);
    }
}
