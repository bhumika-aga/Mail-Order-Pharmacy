package com.pharmacy.subscription.exception;

import org.springframework.http.HttpStatus;

/**
 * An error whose message is safe to show to API clients, with the HTTP status
 * to return. Anything else that escapes a controller becomes a generic 500.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
