package com.furniro.backend.controller;


import com.furniro.backend.domain.products.ProductResponseDTO;
import com.furniro.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size)
    {

    Page<ProductResponseDTO> allProducts = this.productService.getAllProducts(page,size);
    return ResponseEntity.ok(allProducts);
    }
}
