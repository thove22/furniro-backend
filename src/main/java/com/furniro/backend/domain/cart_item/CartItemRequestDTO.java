package com.furniro.backend.domain.cart_item;

import jakarta.validation.constraints.Positive;

public record CartItemRequestDTO(Long productId, @Positive Integer quantity) {
}
