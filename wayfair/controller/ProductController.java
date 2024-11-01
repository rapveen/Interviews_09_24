package com.rental.controller;

import com.rental.domain.Product;
import com.rental.dto.ProductResponse;
import com.rental.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request) {
        Product product = productService.addProduct(request);
        ProductResponse response = ProductResponse.fromProduct(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<RemoveProductResponse> removeProduct(
            @PathVariable String productId,
            @RequestBody RemoveProductRequest request) {
        productService.removeProduct(productId, request.getReason());
        RemoveProductResponse response = new RemoveProductResponse("Product removed successfully.", productId, request.getReason());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available/{type}")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByType(@PathVariable String type) {
        List<Product> products = productService.getAvailableProductsByType(type);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available/price-range")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        List<Product> products = productService.getAvailableProductsByPriceRange(min, max);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Additional endpoints as needed
}