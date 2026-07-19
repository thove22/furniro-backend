package com.furniro.backend.controller;


import com.furniro.backend.domain.products.ProductResponseDTO;
import com.furniro.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(Pageable pageable,
                                                                @RequestParam(required = false) Long categoryId){
    Page<ProductResponseDTO> allProducts = this.productService.getAllProducts(pageable, categoryId);
    return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long id ){
        ProductResponseDTO product = this.productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<List<ProductResponseDTO>> getRelatedProducts(@PathVariable Long id ){
        List<ProductResponseDTO> related = this.productService.getRelatedProducts(id);
        return ResponseEntity.ok(related);
    }
}
