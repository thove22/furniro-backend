package com.furniro.backend.repository;

import com.furniro.backend.domain.categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
