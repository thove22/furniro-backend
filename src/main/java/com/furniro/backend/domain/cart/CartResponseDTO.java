package com.furniro.backend.domain.cart;


import com.furniro.backend.domain.cart_item.CartItemResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(Long cartId, List<CartItemResponseDTO> items,
                              BigDecimal total) {

}
