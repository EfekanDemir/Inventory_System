package com.envanter.inventory.exception;

import java.util.List;

/**
 * Input dogrulama hatasi — HTTP 400 Bad Request.
 * Bos/null alan, negatif miktar gibi durumlar icin firlatilir.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
