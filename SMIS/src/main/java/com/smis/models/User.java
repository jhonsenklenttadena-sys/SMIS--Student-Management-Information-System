package com.smis.models;

public class User {
    public enum Role { ADMIN, FACULTY, STUDENT }

    private int id;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private String contact;
    private boolean isActive;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public Role getRole() { return role; }
    public void setRole(Role r) { this.role = r; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean a) { this.isActive = a; }

    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}
