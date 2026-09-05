package com.furniro.backend.service;


import com.furniro.backend.domain.favorites.Favorite;
import com.furniro.backend.domain.favorites.FavoriteResponseDTO;
import com.furniro.backend.domain.products.Product;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository repository;

    public List<FavoriteResponseDTO> getFavoritedProducts(User user){
        List<Favorite> favorites= repository.findByUserId(user.getId());

        return favorites.stream().map(favorite -> new FavoriteResponseDTO(
                favorite.getProduct().getId(),
                favorite.getProduct().getName(),
                favorite.getProduct().getDescription(),
                favorite.getProduct().getPrice(),
                favorite.getProduct().getStock(),
                favorite.getProduct().getPromotion() != null ?
                        favorite.getProduct().getPromotion().getDiscountPercent() : null ,
                favorite.getProduct().getPromotion()!= null ?
                        favorite.getProduct().getPromotion().getStartDate() : null ,
                favorite.getProduct().getPromotion() != null ?
                        favorite.getProduct().getPromotion().getEndDate() : null,
                favorite.getCreatedAt()
                )).toList();
    }
}
