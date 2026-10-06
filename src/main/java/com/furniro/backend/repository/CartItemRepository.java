package com.furniro.backend.repository;

import com.furniro.backend.domain.cart_item.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
