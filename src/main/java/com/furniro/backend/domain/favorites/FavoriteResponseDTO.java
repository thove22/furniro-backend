package com.furniro.backend.domain.favorites;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FavoriteResponseDTO(Long id, String name, String description, BigDecimal price, Integer stock,
                                  Integer discount, LocalDate startDate, LocalDate endDate, Instant FavoritedAt)  {
}
