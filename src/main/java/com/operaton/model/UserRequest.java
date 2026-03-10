package com.operaton.model;

/**
 * MODEL — Represents the data for a new user request.
 * This is the object that gets passed between Controller → Service.
 */
public class UserRequest {

    private String fullName;
    private String email;
    private String department;
    private String role;

    // ── Constructors ─────────────────────────────────
    public UserRequest() {}

    public UserRequest(String fullName, String email, String department, String role) {
        this.fullName   = fullName;
        this.email      = email;
        this.department = department;
        this.role       = role;
    }

    // ── Getters ──────────────────────────────────────
    public String getFullName()   { return fullName; }
    public String getEmail()      { return email; }
    public String getDepartment() { return department; }
    public String getRole()       { return role; }

    // ── Setters ──────────────────────────────────────
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public void setEmail(String email)           { this.email = email; }
    public void setDepartment(String department) { this.department = department; }
    public void setRole(String role)             { this.role = role; }

    @Override
    public String toString() {
        return "UserRequest{" +
            "fullName='"   + fullName   + '\'' +
            ", email='"    + email      + '\'' +
            ", department='" + department + '\'' +
            ", role='"     + role       + '\'' +
            '}';
    }
}