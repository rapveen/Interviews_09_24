package pricing;

public class BikeSpecificPricingStrategy implements PricingStrategy {
    private final PricingStrategy basePricingStrategy;
    
    @Override
    public double calculatePrice(Product product, int days) {
        double basePrice = basePricingStrategy.calculatePrice(product, days);
        
        if (product instanceof Bike) {
            Bike bike = (Bike) product;
            // Premium for larger sizes
            switch (bike.getSize()) {
                case LARGE: return basePrice * 1.2;
                case MEDIUM: return basePrice * 1.1;
                default: return basePrice;
            }
        }
        return basePrice;
    }
}
