package com.furniro.backend.repository;

import com.furniro.backend.domain.promotions.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromotionsRepository extends JpaRepository<Promotion, UUID> {
}
