package com.envanter.common.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * RegisterRequest nesnesini dogrulayan ornek validator.
 * GenericValidator<T> kullanim ornegi olarak common-lib'e eklendi.
 *
 * Gercek RegisterRequest, user-service'te tanimli oldugu icin
 * bu sinif kullanim sablonu gostermek amaciyla String tipini kullanir.
 */
public class UsernameValidator extends GenericValidator<String> {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;

    @Override
    public ValidationResult validate(String username) {
        List<String> errors = new ArrayList<>();

        // Kural 1: Bos olamaz
        requireNonBlank(errors, username, "Kullanici adi");

        if (username != null) {
            // Kural 2: Minimum uzunluk
            requireMinLength(errors, username, MIN_USERNAME_LENGTH, "Kullanici adi");

            // Kural 3: Maksimum uzunluk
            if (username.length() > MAX_USERNAME_LENGTH) {
                addError(errors, "Kullanici adi en fazla " + MAX_USERNAME_LENGTH + " karakter olmalidir.");
            }

            // Kural 4: Sadece alfanumerik ve alt cizgi
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                addError(errors, "Kullanici adi sadece harf, rakam ve alt cizgi (_) icerebilir.");
            }
        }

        return buildResult(errors);
    }
}
