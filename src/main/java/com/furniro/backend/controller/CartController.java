package com.furniro.backend.controller;

import com.furniro.backend.domain.cart.CartResponseDTO;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
