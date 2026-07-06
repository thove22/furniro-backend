package com.furniro.backend.repository;

import com.furniro.backend.domain.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
