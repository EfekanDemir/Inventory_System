package com.envanter.user.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Tum HTTP hata yanitlarinin standart formati.
 *
 * Ornek JSON:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Kullanici bulunamadi",
 *   "timestamp": "2025-01-01T10:00:00",
 *   "path": "/api/users/99",
 *   "validationErrors": { "username": "bos olamaz" }  // opsiyonel
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    /** Yalnizca 400 dogruluama hatalarinda dolar; diger durumlarda null (JSON'da cikmiyor). */
    private Map<String, String> validationErrors;

    // -- Constructor -----------------------------------------------------------

    public ApiErrorResponse() {}

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
        this.timestamp = LocalDateTime.now();
    }

    // -- Getters & Setters -----------------------------------------------------

    public int getStatus()                            { return status; }
    public void setStatus(int status)                 { this.status = status; }

    public String getError()                          { return error; }
    public void setError(String error)                { this.error = error; }

    public String getMessage()                        { return message; }
    public void setMessage(String message)            { this.message = message; }

    public LocalDateTime getTimestamp()               { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath()                           { return path; }
    public void setPath(String path)                  { this.path = path; }

    public Map<String, String> getValidationErrors()                      { return validationErrors; }
    public void setValidationErrors(Map<String, String> validationErrors)  { this.validationErrors = validationErrors; }
}
