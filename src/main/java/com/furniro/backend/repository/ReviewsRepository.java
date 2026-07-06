package com.furniro.backend.repository;

import com.furniro.backend.domain.reviews.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewsRepository extends JpaRepository<Review, UUID> {
}
