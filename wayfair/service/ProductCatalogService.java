package service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import repository.ProductRepository;

@Service
class ProductCatalogService {
    private final ProductRepository productRepo;
    
    public ProductCatalogService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }
    
    // Get complete catalog of available products
    public ProductCatalog getAvailableProducts() {
        List<Product> products = productRepo.findAvailableForRent();
        Map<String, Long> counts = productRepo.getAvailableProductCounts();
        return new ProductCatalog(products, counts);
    }
    
    // Get available products by type
    public List<Product> getAvailableProductsByType(String productType) {
        return productRepo.findAvailableByType(productType).stream()
        .map(ProductSummary::fromProduct)
        .collect(Collectors.toList());
    }
    
    // Get available products within price range
    public List<Product> getAvailableProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepo.findAvailableByPriceRange(minPrice, maxPrice).stream()
            .map(ProductSummary::fromProduct)
            .collect(Collectors.toList());
    }
}
