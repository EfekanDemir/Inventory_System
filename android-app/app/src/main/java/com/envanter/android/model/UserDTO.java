package com.envanter.android.model;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String token; // Login isleminden sonra dönebilir

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
}
