package com.furniro.backend.controller;

import com.furniro.backend.domain.cart.CartResponseDTO;
import com.furniro.backend.domain.cart_item.CartItemRequestDTO;
import com.furniro.backend.domain.cart_item.UpdateQuantityRequest;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            @AuthenticationPrincipal User user){
       CartResponseDTO cart =  this.cartService.getCart(user);
       return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addCart(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequestDTO cartItemRequestDTO
            ){
            CartResponseDTO cart = cartService.addItem(user, cartItemRequestDTO);
            return ResponseEntity.ok(cart);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestBody UpdateQuantityRequest quantityRequest
            ){
        CartResponseDTO cart = cartService.updateItemQuantity(user, itemId, quantityRequest.quantity());
        return  ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId
    ){
        CartResponseDTO cart = cartService.removeItem(user, itemId);
        return  ResponseEntity.ok(cart);
    }
}
