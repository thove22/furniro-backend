package com.furniro.backend.repository;

import com.furniro.backend.domain.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p From Product p LEFT JOIN FETCH p.promotion pr WHERE p.stock > 0")
    Page<Product> findAllAvailable(Pageable pageable);

    @Query("Select p From Product p WHERE p.category.id = :categoryId AND p.id != :productId  AND p.stock > 0 ")
    List<Product> findRelatedProducts(@Param("categoryId") Long categoryId,
                                      @Param("productId") Long productId,
                                      Pageable pageable);


    @Query("Select p From Product p Where p.stock > 0 " +
            " AND  (:categoryId IS NULL OR p.category.id = :categoryId)" +
            " AND (:search IS NULL OR lower(p.name) LIKE LOWER(CAST(:search AS string)))" +
            " AND (:minPrice IS NULL OR p.price >= :minPrice)" +
            " AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findFilteredProducts(@Param("categoryId") Long categoryId,
                                       @Param("search") String search,
                                       @Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice,
                                       Pageable pageable);


}
