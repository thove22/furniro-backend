package com.furniro.backend.service;


import com.furniro.backend.domain.products.Product;
import com.furniro.backend.domain.products.ProductResponseDTO;
import com.furniro.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductResponseDTO> getAllProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = this.productRepository.findAllAvailable(pageable);
        return productPage.map(product -> new ProductResponseDTO(
                 product.getId(),
                 product.getName(),
                 product.getDescription(),
                 product.getPrice(),
                 product.getStock(),
                 product.getPromotion() != null ? product.getPromotion().getDiscountPercent() : null,
                 product.getPromotion() != null ? product.getPromotion().getStartDate() : null,
                 product.getPromotion() != null ? product.getPromotion().getEndDate() : null
        ));
    }
}
