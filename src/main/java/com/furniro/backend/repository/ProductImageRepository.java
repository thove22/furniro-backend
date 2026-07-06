package com.furniro.backend.repository;

import com.furniro.backend.domain.product_image.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
