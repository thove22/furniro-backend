package com.furniro.backend.domain.users;

public enum UserRole {
    USER("user"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) { this.role = role; };

    public String getRole() {
        return role;
    }
}
