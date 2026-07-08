package com.furniro.backend.repository;

import com.furniro.backend.domain.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p From Product p LEFT JOIN FETCH p.promotion pr WHERE p.stock > 0")
    Page<Product> findAllAvailable(Pageable pageable);

}
