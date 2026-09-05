package com.furniro.backend.repository;

import com.furniro.backend.domain.favorites.Favorite;
import com.furniro.backend.domain.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("Select f from Favorite f WHERE f.user.id = :userId")
    List<Favorite> findByUserId(@Param("userId") Long userId);
}
