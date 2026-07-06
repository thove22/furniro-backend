package com.furniro.backend.repository;

import com.furniro.backend.domain.cart_item.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
