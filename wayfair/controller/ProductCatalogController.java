package controller;

@RestController
@RequestMapping("/api/catalog")
class ProductCatalogController {
    private final ProductCatalogService catalogService;
    
    @GetMapping("/available")
    public ProductCatalog getAvailableProducts() {
        return catalogService.getAvailableProducts();
    }
    
    @GetMapping("/available/{type}")
    public List<ProductSummary> getAvailableProductsByType(@PathVariable String type) {
        return catalogService.getAvailableProductsByType(type).stream()
            .map(ProductSummary::fromProduct)
            .collect(Collectors.toList());
    }
    
    @GetMapping("/available/price-range")
    public List<ProductSummary> getAvailableProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        return catalogService.getAvailableProductsByPriceRange(min, max).stream()
            .map(ProductSummary::fromProduct)
            .collect(Collectors.toList());
    }
}

