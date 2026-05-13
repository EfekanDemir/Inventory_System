package com.envanter.common.generic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tüm mikroservis API yanıtlarını saran genel sarmalayıcı.
 *
 * Kullanım:
 *   GenericResponseWrapper<UserDTO> response = GenericResponseWrapper.success(userDTO);
 *   GenericResponseWrapper<Void>    error    = GenericResponseWrapper.error("Kullanici bulunamadi");
 *
 * @param <T> Sarmalanan veri tipi
 */
public final class GenericResponseWrapper<T> {

    private final T data;
    private final String message;
    private final String timestamp;
    private final boolean success;

    // -- Constructor (private -- static factory ile olusturulur) ---------------

    private GenericResponseWrapper(T data, String message, boolean success) {
        this.data      = data;
        this.message   = message;
        this.success   = success;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // -- Static Factory Methods ------------------------------------------------

    /**
     * Basarili yanit olusturur.
     *
     * @param data    Donecek veri
     * @param message Bilgilendirme mesaji
     * @param <T>     Veri tipi
     */
    public static <T> GenericResponseWrapper<T> success(T data, String message) {
        return new GenericResponseWrapper<>(data, message, true);
    }

    /**
     * Basarili yanit olusturur (varsayilan mesajla).
     *
     * @param data Donecek veri
     * @param <T>  Veri tipi
     */
    public static <T> GenericResponseWrapper<T> success(T data) {
        return new GenericResponseWrapper<>(data, "Islem basarili.", true);
    }

    /**
     * Hata yaniti olusturur (data=null).
     *
     * @param message Hata mesaji
     * @param <T>     Veri tipi
     */
    public static <T> GenericResponseWrapper<T> error(String message) {
        return new GenericResponseWrapper<>(null, message, false);
    }

    // -- Builder (opsiyonel -- karmasik durumlar icin) --------------------------

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private T data;
        private String message;
        private boolean success;

        private Builder() {}

        public Builder<T> data(T data)          { this.data = data;       return this; }
        public Builder<T> message(String msg)   { this.message = msg;     return this; }
        public Builder<T> success(boolean s)    { this.success = s;       return this; }

        public GenericResponseWrapper<T> build() {
            return new GenericResponseWrapper<>(data, message, success);
        }
    }

    // -- Getters ---------------------------------------------------------------

    public T getData()                { return data;      }
    public String getMessage()        { return message;   }
    public String getTimestamp()      { return timestamp; }
    public boolean isSuccess()        { return success;   }

    @Override
    public String toString() {
        return "GenericResponseWrapper{"
                + "success=" + success
                + ", message='" + message + '\''
                + ", timestamp='" + timestamp + '\''
                + ", data=" + data
                + '}';
    }
}
