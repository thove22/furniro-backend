package com.furniro.backend.service;


import com.furniro.backend.domain.favorites.Favorite;
import com.furniro.backend.domain.favorites.FavoriteRequestDTO;
import com.furniro.backend.domain.favorites.FavoriteResponseDTO;
import com.furniro.backend.domain.products.Product;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.exception.DuplicateResourceException;
import com.furniro.backend.exception.ResourceNotFoundException;
import com.furniro.backend.repository.FavoriteRepository;
import com.furniro.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository repository;

    @Autowired
    private ProductRepository productRepository;

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

    public void favoriteProduct(User user, FavoriteRequestDTO request){
        Product product = productRepository.findById(request.productId()).
                orElseThrow(()-> new ResourceNotFoundException("Product Not found with id: " + request.productId()));
        boolean alreadyFavorite = repository.existsByUserIdAndProductId(user.getId(), product.getId());

        if(alreadyFavorite){
            throw new DuplicateResourceException("Product Already Favorited");
        }
        Favorite favorite = new Favorite();
        favorite.setProduct(product);
        favorite.setUser(user);
        repository.save(favorite);
    }

    @Transactional
    public void removeFavorite(User user, Long productId){
       repository.deleteByUserIdAndProductId(user.getId(),
               productId);
    }

}
