package com.furniro.backend.domain.products;

import com.furniro.backend.domain.categories.Category;
import com.furniro.backend.domain.promotions.Promotion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Table (name = "products")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    private Integer stock;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
}
