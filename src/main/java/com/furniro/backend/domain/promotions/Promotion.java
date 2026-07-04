package com.furniro.backend.domain.promotions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;


@Table(name  = "promotions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;

    @CreationTimestamp
    private Instant createdAt;

}
