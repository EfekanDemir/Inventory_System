package com.envanter.notification.exception;

import java.util.List;

/**
 * Gecersiz bildirim tipi veya bos alan durumunda firlatilir.
 * HTTP 400 Bad Request.
 */
public class NotificationValidationException extends RuntimeException {

    private final List<String> errors;

    public NotificationValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public NotificationValidationException(String message, List<String> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
