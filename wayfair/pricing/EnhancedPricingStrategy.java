package pricing;

public class EnhancedPricingStrategy implements PricingStrategy {
    private final List<DiscountRule> discountRules;
    
    public EnhancedPricingStrategy() {
        this.discountRules = Arrays.asList(
            new DiscountRule(30, 0.25),  // 25% off for monthly rentals
            new DiscountRule(14, 0.15),  // 15% off for 2 weeks
            new DiscountRule(7, 0.10)    // 10% off for 1 week
        );
    }
    
    @Override
    public double calculatePrice(Product product, int days) {
        double basePrice = product.getBasePrice() * days;
        
        // Find the highest applicable discount
        return discountRules.stream()
            .filter(rule -> rule.applies(days))
            .map(DiscountRule::getDiscount)
            .max(Double::compareTo)
            .map(discount -> basePrice * (1 - discount))
            .orElse(basePrice);
    }
}
