package com.furniro.backend.service;


import com.furniro.backend.domain.products.Product;
import com.furniro.backend.domain.products.ProductResponseDTO;
import com.furniro.backend.exception.ResourceNotFoundException;
import com.furniro.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private ProductResponseDTO mapToDTO(Product product){
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getPromotion() != null ? product.getPromotion().getDiscountPercent() : null,
                product.getPromotion() != null ? product.getPromotion().getStartDate() : null,
                product.getPromotion() != null ? product.getPromotion().getEndDate() : null
        );
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable, Long categoryId,
                                    String search, BigDecimal minPrice,
                                                   BigDecimal maxPrice){

        String searchPattern = search != null ? "%" + search + "%" : null;

        if(categoryId != null || searchPattern != null || minPrice != null || maxPrice != null){
            Page<Product> productPageCategory = this.productRepository.findFilteredProducts(categoryId,
                    searchPattern, minPrice, maxPrice, pageable);
            return productPageCategory.map(this::mapToDTO);
        }else {
            Page<Product> productPage = this.productRepository.findAllAvailable(pageable);
            return productPage.map(this::mapToDTO);
        }

    }

    public ProductResponseDTO getProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product Not Found with id: " + id));
        return mapToDTO(product);
    }
    public List<ProductResponseDTO>getRelatedProducts(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product Not Found with id: " + id));

        Pageable limit = PageRequest.of(0, 4);
        List<Product> related = productRepository.findRelatedProducts(product.getCategory().getId(),
                id, limit);
        return related.stream().map(this::mapToDTO).toList();
    }
}
