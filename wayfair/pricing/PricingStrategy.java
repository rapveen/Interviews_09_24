package pricing;

// Services using Strategy pattern for pricing
interface PricingStrategy {
    double calculatePrice(Product product, int days);
}