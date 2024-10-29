package domain;

// Product hierarchy using Template Method pattern
abstract class Product {
    private String id;
    private String name;
    private double basePrice;
    private boolean isAvailable;
    private String description;
    private LocalDateTime lastMaintenanceDate;
    
    public abstract String getProductType();
    public abstract ProductDetails getDetails();
    
    // Template method for calculating rental price
    public final double calculateRentalPrice(int days) {
        double price = basePrice * days;
        return applyDiscount(price, days);
    }
    
    // Hook method for discount calculation
    protected double applyDiscount(double price, int days) {
        if (days >= 7) return price * 0.9;  // 10% discount for week or more
        return price;
    }

    public boolean isAvailableForRent() {
        return isAvailable && isMaintenanceValid();
    }
    
    private boolean isMaintenanceValid() {
        return lastMaintenanceDate != null && 
               lastMaintenanceDate.plusMonths(1).isAfter(LocalDateTime.now());
    }
    
    // Getters and setters
}
