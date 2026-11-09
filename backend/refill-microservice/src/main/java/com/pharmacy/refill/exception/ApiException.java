package com.pharmacy.refill.exception;

import org.springframework.http.HttpStatus;

/**
 * An error whose message is safe to show to API clients, with the HTTP status
 * to return. Anything else that escapes a controller becomes a generic 500.
 */
public class ApiException extends RuntimeException {
    
    private final HttpStatus status;
    private final String error;
    
    public ApiException(HttpStatus status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }
    
    public ApiException(HttpStatus status, String error, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.error = error;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    /**
     * Short error category, e.g. "Stock Unavailable" (kept for existing API clients).
     */
    public String getError() {
        return error;
    }
}
