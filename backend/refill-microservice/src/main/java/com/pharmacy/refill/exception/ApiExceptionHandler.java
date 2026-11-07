package com.pharmacy.refill.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Renders {@link ApiException} as {@code {"error": ..., "message": ...}} (the
 * response shape this service already used). Other exceptions are left to
 * Spring: security failures keep their 401/403, and anything unexpected becomes
 * a 500 without the exception message.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus())
                   .body(Map.of("error", e.getError(), "message", e.getMessage()));
    }
}
