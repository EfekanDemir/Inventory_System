package com.envanter.common.generic;

import java.util.List;

/**
 * Dogrulama sonucunu tutan immutable sinif.
 * GenericValidator.validate() metodu tarafindan uretilir.
 */
public final class ValidationResult {

    private final List<String> errors;

    // -- Constructor (package-private -- sadece GenericValidator olusturur) ----

    ValidationResult(List<String> errors) {
        this.errors = List.copyOf(errors); // immutable kopya
    }

    // -- API ------------------------------------------------------------------

    /**
     * Hic hata yoksa true doner.
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Dogrulama hatalarinin listesi (degistirilemez).
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Hata sayisi.
     */
    public int errorCount() {
        return errors.size();
    }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + isValid() + ", errors=" + errors + '}';
    }
}
