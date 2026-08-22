package com.furniro.backend.repository;

import com.furniro.backend.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserRepository extends JpaRepository<User, Long> {
        UserDetails findByEmail(String email);
}
