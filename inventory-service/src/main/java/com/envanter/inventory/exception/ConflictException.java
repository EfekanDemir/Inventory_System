package com.envanter.inventory.exception;

/**
 * Yeterli stok olmadigi ya da kaynak cakismasi durumu — HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
