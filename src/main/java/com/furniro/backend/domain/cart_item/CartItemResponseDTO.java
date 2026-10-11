package com.furniro.backend.domain.cart_item;

import java.math.BigDecimal;

public record CartItemResponseDTO(Long itemId, Long productId, String productName,
                                  BigDecimal unitPrice, Integer quantity,
                                  BigDecimal linetotal) {
}
