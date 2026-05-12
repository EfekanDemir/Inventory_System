package com.envanter.user.exception;

/**
 * Thrown when authentication fails (wrong password, expired token, etc.).
 * Maps to HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
