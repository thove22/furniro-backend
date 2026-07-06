package com.furniro.backend.repository;

import com.furniro.backend.domain.cart_item.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
