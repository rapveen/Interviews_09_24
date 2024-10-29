package repository;

package com.rental.repository;

import com.rental.domain.Product;
import com.rental.domain.Size;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository {
    void add(Product product);
    void remove(String productId);
    Optional<Product> findById(String productId);
    List<Product> findAll();
    List<Product> findByType(String type);
    List<Product> findAvailable();
    long countBikesBySize(Size size);
    long countAvailableBikesBySize(Size size);
    Map<Size, Long> getBikesCountBySize();
    Map<Size, Long> getAvailableBikesCountBySize();
    List<Product> findAvailableForRent();
    List<Product> findAvailableByType(String productType);
    Map<String, Long> getAvailableProductCounts();
    List<Product> findAvailableByPriceRange(double minPrice, double maxPrice);
}

// Similar repository interfaces for CustomerRepository and RentalRepository

