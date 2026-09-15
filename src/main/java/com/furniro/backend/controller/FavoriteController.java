package com.furniro.backend.controller;

import com.furniro.backend.domain.favorites.FavoriteRequestDTO;
import com.furniro.backend.domain.favorites.FavoriteResponseDTO;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService service;

    @GetMapping
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(
            @AuthenticationPrincipal User user){
          List<FavoriteResponseDTO> favorites = this.service.getFavoritedProducts(user);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FavoriteRequestDTO request
    ){
            this.service.favoriteProduct(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId
    ){
        this.service.removeFavorite(user, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
