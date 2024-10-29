package dto;

// Value object for catalog representation
class ProductCatalog {
    private final List<Product> availableProducts;
    private final Map<String, Long> productCounts;
    
    public ProductCatalog(List<Product> products, Map<String, Long> counts) {
        this.availableProducts = products;
        this.productCounts = counts;
    }
    
    public List<ProductSummary> getProductSummaries() {
        return availableProducts.stream()
            .map(ProductSummary::fromProduct)
            .collect(Collectors.toList());
    }
    
    public Map<String, Long> getProductCounts() {
        return new HashMap<>(productCounts);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Available Products for Rent:\n\n");
        
        // Add count summary
        sb.append("Product Counts:\n");
        productCounts.forEach((type, count) -> 
            sb.append(String.format("- %s: %d available\n", type, count))
        );
        
        // Add detailed product listings
        sb.append("\nDetailed Listings:\n");
        getProductSummaries().forEach(summary -> 
            sb.append(summary.toString()).append("\n")
        );
        
        return sb.toString();
    }
}
