package pricing;

public class CompositePricingStrategy implements PricingStrategy {
    private final List<PricingStrategy> strategies;
    
    @Override
    public double calculatePrice(Product product, int days) {
        double basePrice = product.getBasePrice() * days;
        
        for (PricingStrategy strategy : strategies) {
            basePrice = strategy.calculatePrice(product, days);
        }
        
        return basePrice;
    }
}