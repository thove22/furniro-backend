package com.furniro.backend.repository;

import com.furniro.backend.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
