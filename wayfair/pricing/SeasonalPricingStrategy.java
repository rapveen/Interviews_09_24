class SeasonalPricingStrategy implements PricingStrategy {
    private final PricingStrategy basePricingStrategy;
    
    private final Map<Month, Double> seasonalMultipliers = Map.of(
        Month.JUNE, 1.2,
        Month.JULY, 1.3,
        Month.AUGUST, 1.3,
        Month.DECEMBER, 1.2
    );
    
    @Override
    public double calculatePrice(Product product, int days) {
        double basePrice = basePricingStrategy.calculatePrice(product, days);
        Month currentMonth = LocalDate.now().getMonth();
        return basePrice * seasonalMultipliers.getOrDefault(currentMonth, 1.0);
    }
}