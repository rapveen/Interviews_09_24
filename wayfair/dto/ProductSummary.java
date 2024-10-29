package dto;

// Value object for product summary
class ProductSummary {
    private final String id;
    private final String name;
    private final String type;
    private final double price;
    private final ProductDetails details;
    
    public static ProductSummary fromProduct(Product product) {
        return new ProductSummary(
            product.getId(),
            product.getName(),
            product.getProductType(),
            product.getBasePrice(),
            product.getDetails()
        );
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)\n  Price: $%.2f\n  %s",
            name, type, price, details);
    }
}

