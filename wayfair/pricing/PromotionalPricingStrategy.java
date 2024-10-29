package pricing;

public class PromotionalPricingStrategy implements PricingStrategy {
    private final PricingStrategy basePricingStrategy;
    private final List<PromotionRule> activePromotions;
    
    @Override
    public double calculatePrice(Product product, int days) {
        double basePrice = basePricingStrategy.calculatePrice(product, days);
        
        return activePromotions.stream()
            .filter(promo -> promo.isApplicable(product, days))
            .map(promo -> promo.applyPromotion(basePrice))
            .min(Double::compareTo)
            .orElse(basePrice);
    }
}
