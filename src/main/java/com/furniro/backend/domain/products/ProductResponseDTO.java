package com.furniro.backend.domain.products;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductResponseDTO(Long id, String name, String description, BigDecimal price, Integer stock,
                                 Integer discount, LocalDate startDate, LocalDate endDate) {
}
