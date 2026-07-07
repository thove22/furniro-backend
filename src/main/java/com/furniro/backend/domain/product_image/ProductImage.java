package com.furniro.backend.domain.product_image;

import com.furniro.backend.domain.products.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Table(name = "product_images")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private Integer position;
    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
