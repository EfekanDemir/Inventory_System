package com.envanter.inventory.exception;

/**
 * Yetkilendirme hatasi — kimlik dogru ama izin yok.
 * HTTP 403 Forbidden.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
