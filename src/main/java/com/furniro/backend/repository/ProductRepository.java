package com.furniro.backend.repository;

import com.furniro.backend.domain.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p From Product p LEFT JOIN FETCH p.promotion pr WHERE p.stock > 0")
    Page<Product> findAllAvailable(Pageable pageable);

    @Query("Select p From Product p WHERE p.category.id = :categoryId AND p.id != :productId  AND p.stock > 0 ")
    List<Product> findRelatedProducts(@Param("categoryId") Long categoryId,
                                      @Param("productId") Long productId,
                                      Pageable pageable);

}
