package com.furniro.backend.domain.users;

public record RegisterDTO(String firstname, String lastname, String email,
                         String password, UserRole role) {
}
