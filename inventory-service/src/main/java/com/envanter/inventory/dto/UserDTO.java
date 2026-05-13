package com.envanter.inventory.dto;

/**
 * user-service'ten dönen kullanıcı verisi.
 * UserClient.getUserById() tarafından kullanılır.
 */
public class UserDTO {

    private Long   id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public UserDTO() {}

    public Long   getId()              { return id; }
    public void   setId(Long v)        { this.id = v; }

    public String getUsername()        { return username; }
    public void   setUsername(String v){ this.username = v; }

    public String getEmail()           { return email; }
    public void   setEmail(String v)   { this.email = v; }

    public String getFirstName()           { return firstName; }
    public void   setFirstName(String v)   { this.firstName = v; }

    public String getLastName()            { return lastName; }
    public void   setLastName(String v)    { this.lastName = v; }

    public String getRole()            { return role; }
    public void   setRole(String v)    { this.role = v; }
}
