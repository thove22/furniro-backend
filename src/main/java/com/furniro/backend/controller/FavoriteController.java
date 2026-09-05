package com.furniro.backend.controller;

import com.furniro.backend.domain.favorites.FavoriteResponseDTO;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
