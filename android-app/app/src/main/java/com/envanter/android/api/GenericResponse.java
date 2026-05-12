package com.envanter.android.api;

public class GenericResponse<T> {
    private T data;
    private String message;
    private boolean success;
    private String timestamp;

    public T getData() { return data; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
