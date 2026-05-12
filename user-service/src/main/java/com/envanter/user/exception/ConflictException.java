package com.envanter.user.exception;

/**
 * Thrown when a resource already exists (e.g. duplicate username).
 * Maps to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
