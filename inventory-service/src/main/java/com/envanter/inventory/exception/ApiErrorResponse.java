package com.envanter.inventory.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Tum HTTP hata yanitlarinin standart formati — inventory-service.
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

    public ApiErrorResponse() {}

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
        this.timestamp = LocalDateTime.now();
    }

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
