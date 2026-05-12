package com.envanter.common.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Genel dogrulama soyut sinifi.
 *
 * Alt siniflar {@code validate(T object)} metodunu override ederek
 * nesneye ozgu dogrulama kurallarini tanimlar.
 *
 * Kullanim:
 * <pre>
 *   public class RegisterRequestValidator extends GenericValidator<RegisterRequest> {
 *
 *       {@literal @}Override
 *       public ValidationResult validate(RegisterRequest request) {
 *           List<String> errors = new ArrayList<>();
 *
 *           if (request.getUsername() == null || request.getUsername().isBlank())
 *               addError(errors, "Kullanici adi bos olamaz");
 *
 *           if (request.getPassword().length() < 8)
 *               addError(errors, "Sifre en az 8 karakter olmalidir");
 *
 *           return buildResult(errors);
 *       }
 *   }
 * </pre>
 *
 * @param <T> Dogrulanacak nesnenin tipi
 */
public abstract class GenericValidator<T> {

    /**
     * Verilen nesneyi dogrular ve {@link ValidationResult} doner.
     *
     * @param object Dogrulanacak nesne
     * @return ValidationResult (hata listesi veya bos liste)
     */
    public abstract ValidationResult validate(T object);

    // -- Yardimci Metodlar (alt siniflar kullanir) ----------------------------

    /**
     * Hata listesine yeni bir hata mesaji ekler.
     */
    protected void addError(List<String> errors, String message) {
        if (message != null && !message.isBlank()) {
            errors.add(message);
        }
    }

    /**
     * Null/bos alan kontrolu.
     * Hatali ise hata listesine ekler.
     */
    protected void requireNonBlank(List<String> errors, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            addError(errors, fieldName + " bos olamaz.");
        }
    }

    /**
     * Minimum uzunluk kontrolu.
     */
    protected void requireMinLength(List<String> errors, String value,
                                    int min, String fieldName) {
        if (value != null && value.length() < min) {
            addError(errors, fieldName + " en az " + min + " karakter olmalidir.");
        }
    }

    /**
     * Pozitif sayi kontrolu.
     */
    protected void requirePositive(List<String> errors, Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            addError(errors, fieldName + " pozitif bir deger olmalidir.");
        }
    }

    /**
     * Hata listesinden ValidationResult olusturur.
     */
    protected ValidationResult buildResult(List<String> errors) {
        return new ValidationResult(errors);
    }

    /**
     * Bos (gecerli) bir ValidationResult doner.
     */
    protected ValidationResult valid() {
        return new ValidationResult(new ArrayList<>());
    }
}
