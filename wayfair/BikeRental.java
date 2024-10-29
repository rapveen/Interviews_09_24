import java.util.*;

// Value objects for product details
interface ProductDetails {}

// Domain Models
enum Size { SMALL, MEDIUM, LARGE }
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


class Bike extends Product {
    private Size size;
    
    @Override
    public String getProductType() {
        return "Bike-" + size;
    }

    public Size getSize() {
        return size;
    }
}

enum ScooterType { ELECTRIC, GAS }

class Scooter extends Product {
    private ScooterType type;
    
    @Override
    public String getProductType() {
        return "Scooter-" + type;
    }
}


class BikeDetails implements ProductDetails {
    private final Size size;
    private final String frameType;
    private final int weightKg;
    
    // Constructor and getters
    
    @Override
    public String toString() {
        return String.format("Size: %s, Frame: %s, Weight: %dkg", 
            size, frameType, weightKg);
    }
}

class ScooterDetails implements ProductDetails {
    private final ScooterType type;
    private final int maxSpeedKph;
    private final int rangeKm;
    
    // Constructor and getters
    
    @Override
    public String toString() {
        return String.format("Type: %s, Max Speed: %dkph, Range: %dkm", 
            type, maxSpeedKph, rangeKm);
    }
}

class Customer {
    private String id;
    private String name;
    private Size size;
    private List<Rental> rentals;
    private double balance;
    
    // Constructor
    public Customer(String id, String name, Size size) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.rentals = new ArrayList<>();
        this.balance = 0.0;
    }
    
    public Size getSize() {
        return size;
    }
    // Getters and setters
}

class BikeRenterInfo {
    private final String customerId;
    private final String customerName;
    private final Size preferredSize;
    private final List<String> rentedBikeIds;
    private final LocalDateTime earliestRentalDate;
    
    // Constructor and getters
}

class SizeMismatchException extends RuntimeException {
    public SizeMismatchException(String message) {
        super(message);
    }
}

// Define discount rules
class DiscountRule {
    private final int minimumDays;
    private final double discountPercentage;
    
    public DiscountRule(int minimumDays, double discountPercentage) {
        this.minimumDays = minimumDays;
        this.discountPercentage = discountPercentage;
    }
    
    public boolean applies(int days) {
        return days >= minimumDays;
    }
    
    public double getDiscount() {
        return discountPercentage;
    }
}

class Rental {
    private String id;
    private Product product;
    private Customer customer;
    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;
    private boolean isReturned;
    
    Rental() {

    }
    // Getters and setters
}

// Added size validation strategy using Strategy pattern
interface BikeValidationStrategy {
    boolean isValidRental(Bike bike, Customer customer);
}

class SizeMatchValidationStrategy implements BikeValidationStrategy {
    @Override
    public boolean isValidRental(Bike bike, Customer customer) {
        return bike.getSize() == customer.getSize();
    }
}

// Repositories using Repository pattern
interface ProductRepository {
    void add(Product product);
    void remove(String productId);
    List<Product> findAll();
    List<Product> findByType(String type);
    List<Product> findAvailable();

    // Add specific queries for bike inventory
    long countBikesBySize(Size size);
    long countAvailableBikesBySize(Size size);
    
    // Optional: More detailed inventory statistics
    Map<Size, Long> getBikesCountBySize();
    Map<Size, Long> getAvailableBikesCountBySize();

    List<Product> findAvailableForRent();
    List<Product> findAvailableByType(String productType);
    Map<String, Long> getAvailableProductCounts();
    List<Product> findAvailableByPriceRange(double minPrice, double maxPrice);
}

interface CustomerRepository {
    void add(Customer customer);
    Customer findById(String id);
    List<Customer> findAll();
}

interface RentalRepository {
    void add(Rental rental);
    List<Rental> findByCustomer(String customerId);
    List<Rental> findOverdue();
    List<Rental> findActive();
    @Query("""
        SELECT new com.rental.dto.RentalSummary(
            r.id, 
            r.product.id,
            r.product.name,
            r.product.productType,
            r.customer.id,
            r.customer.name,
            r.rentedAt,
            r.dueDate
        )
        FROM Rental r
        WHERE r.isReturned = false
        ORDER BY r.dueDate ASC
        """)
    List<RentalSummary> findActiveRentalsWithDetails();
    
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.isReturned = false GROUP BY r.product.productType")
    Map<String, Long> getActiveRentalCountsByType();

    @Query("""
        SELECT new com.rental.dto.OverdueRentalInfo(
            r.product.id,
            r.product.name,
            r.product.productType,
            r.customer.id,
            r.customer.name,
            r.customer.email,
            r.customer.phone,
            r.rentedAt,
            r.dueDate
        )
        FROM Rental r
        WHERE r.isReturned = false 
        AND r.dueDate < CURRENT_TIMESTAMP
        ORDER BY r.dueDate ASC
        """)
    List<OverdueRentalInfo> findOverdueRentalsWithDetails();
    @Query("""
        SELECT DISTINCT r.customer FROM Rental r 
        WHERE r.isReturned = false 
        AND r.product.productType LIKE 'Bike%'
    """)
    List<Customer> findCurrentBikeRenters();
    // Updated repository method
    @Query("""
        SELECT new com.rental.dto.BikeRenterInfo(
            c.id, 
            c.name, 
            c.size,
            GROUP_CONCAT(r.product.id),
            MIN(r.rentedAt)
        )
        FROM Rental r 
        JOIN r.customer c
        WHERE r.isReturned = false 
        AND r.product.productType LIKE 'Bike%'
        GROUP BY c.id, c.name, c.size
    """)
    List<BikeRenterInfo> findCurrentBikeRentersWithDetails();
    @Query("""
        SELECT DISTINCT c FROM Customer c 
        JOIN c.rentals r 
        WHERE r.isReturned = false 
        AND r.product.productType LIKE 'Bike%'
        AND c.size = :size
    """)
    List<Customer> findCurrentBikeRentersBySize(Size size);
    
    @Query("""
        SELECT DISTINCT c FROM Customer c 
        JOIN c.rentals r 
        WHERE r.isReturned = false 
        AND r.product.productType LIKE 'Bike%'
        AND r.rentedAt >= :since
    """)
    List<Customer> findCurrentBikeRentersSince(LocalDateTime since);
}

// Services using Strategy pattern for pricing
interface PricingStrategy {
    double calculatePrice(Product product, int days);
}

class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Product product, int days) {
        return product.calculateRentalPrice(days);
    }
}

// Enhanced pricing strategy
class EnhancedPricingStrategy implements PricingStrategy {
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

class BikeSpecificPricingStrategy implements PricingStrategy {
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

class PromotionalPricingStrategy implements PricingStrategy {
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

class CompositePricingStrategy implements PricingStrategy {
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

// Value objects for inventory reporting
class InventoryCount {
    private final long totalCount;
    private final long availableCount;
    
    public InventoryCount(long totalCount, long availableCount) {
        this.totalCount = totalCount;
        this.availableCount = availableCount;
    }
    
    public long getTotalCount() {
        return totalCount;
    }
    
    public long getAvailableCount() {
        return availableCount;
    }
    
    public long getRentedCount() {
        return totalCount - availableCount;
    }
    
    @Override
    public String toString() {
        return String.format("Total: %d, Available: %d, Rented: %d", 
            totalCount, availableCount, getRentedCount());
    }
}

// Add this new DTO class
class RentalSummary {
    private final String rentalId;
    private final String productId;
    private final String productName;
    private final String productType;
    private final String customerId;
    private final String customerName;
    private final LocalDateTime rentedAt;
    private final LocalDateTime dueDate;
    
    // Constructor matching the query projection
    
    public Duration getRentalDuration() {
        return Duration.between(rentedAt, LocalDateTime.now());
    }
    
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate);
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s (Type: %s) - Rented by %s\n" +
            "  Rented: %s\n" +
            "  Due: %s\n" +
            "  Status: %s",
            productName,
            productType,
            customerName,
            rentedAt.format(DateTimeFormatter.ISO_LOCAL_DATE),
            dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            isOverdue() ? "OVERDUE" : "Active"
        );
    }
}

// New DTO for overdue rentals
class OverdueRentalInfo {
    private final String productId;
    private final String productName;
    private final String productType;
    private final String customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerPhone;
    private final LocalDateTime rentedAt;
    private final LocalDateTime dueDate;
    
    // Constructor and getters
    
    public Duration getOverdueDuration() {
        return Duration.between(dueDate, LocalDateTime.now());
    }
    
    public String getOverdueStatus() {
        long daysOverdue = getOverdueDuration().toDays();
        if (daysOverdue > 7) return "SEVERELY_OVERDUE";
        if (daysOverdue > 3) return "MODERATELY_OVERDUE";
        return "SLIGHTLY_OVERDUE";
    }
    
    @Override
    public String toString() {
        return String.format(
            "OVERDUE RENTAL - %s (%s)\n" +
            "Customer: %s\n" +
            "Contact: Email: %s, Phone: %s\n" +
            "Rented: %s\n" +
            "Due Date: %s\n" +
            "Days Overdue: %d\n" +
            "Status: %s",
            productName,
            productType,
            customerName,
            customerEmail,
            customerPhone,
            rentedAt.format(DateTimeFormatter.ISO_LOCAL_DATE),
            dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            getOverdueDuration().toDays(),
            getOverdueStatus()
        );
    }
}


// Service layer using Facade pattern
@Service
class RentalService {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final RentalRepository rentalRepo;
    private final PricingStrategy pricingStrategy;
    private final BikeValidationStrategy bikeValidationStrategy;
    
    public RentalService(ProductRepository productRepo, 
                        CustomerRepository customerRepo,
                        RentalRepository rentalRepo,
                        PricingStrategy pricingStrategy,
                        BikeValidationStrategy bikeValidationStrategy) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.rentalRepo = rentalRepo;
        // this.pricingStrategy = pricingStrategy;
        this.pricingStrategy = new CompositePricingStrategy(Arrays.asList(
            new EnhancedPricingStrategy(),
            new SeasonalPricingStrategy(),
            new BikeSpecificPricingStrategy(),
            new PromotionalPricingStrategy()
        ));
        this.bikeValidationStrategy = bikeValidationStrategy;
    }
    
    // Query methods
    // Specific method to answer "How many small bikes do you have?"
    public InventoryCount getSmallBikesCount() {
        long totalCount = productRepo.countBikesBySize(Size.SMALL);
        long availableCount = productRepo.countAvailableBikesBySize(Size.SMALL);
        
        return new InventoryCount(totalCount, availableCount);
    }
    
    // Get inventory count for any bike size
    public InventoryCount getBikesCountBySize(Size size) {
        long totalCount = productRepo.countBikesBySize(size);
        long availableCount = productRepo.countAvailableBikesBySize(size);
        
        return new InventoryCount(totalCount, availableCount);
    }
    
    // Get complete bike inventory report
    public BikeInventoryReport getFullBikeInventoryReport() {
        Map<Size, Long> totalCounts = productRepo.getBikesCountBySize();
        Map<Size, Long> availableCounts = productRepo.getAvailableBikesCountBySize();
        
        return new BikeInventoryReport(totalCounts, availableCounts);
    }
    
    public List<Product> getAvailableProducts() {
        return productRepo.findAvailable();
    }
    
    public double getCustomerBalance(String customerId) {
        return customerRepo.findById(customerId).getBalance();
    }
    
    public List<Rental> getRentedProducts() {
        return rentalRepo.findActive();
    }
    
    public List<Rental> getOverdueRentals() {
        return rentalRepo.findOverdue();
    }
    
    public List<Rental> getCustomerRentals(String customerId) {
        return rentalRepo.findByCustomer(customerId);
    }
    
    public List<Customer> getBikeRenters() {
        return rentalRepo.findActive().stream()
            .filter(r -> r.getProduct() instanceof Bike)
            .map(Rental::getCustomer)
            .distinct()
            .collect(Collectors.toList());
    }
    
    // Command methods
    @Transactional
    public void addProduct(Product product) {
            // Validate product details
        if (product.getBasePrice() <= 0) {
            throw new InvalidProductException("Product price must be positive");
        }
        
        // Set initial state
        product.setAvailable(true);
        product.setLastMaintenanceDate(LocalDateTime.now());
        
        // Save and index product
        productRepo.add(product);
        
        // Audit logging
        auditService.logProductAdded(product);
    }
    
    @Transactional
    public void addCustomer(Customer customer) {
         // Validate customer details
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new InvalidCustomerException("Customer name is required");
        }
        
        // Initialize customer state
        customer.setRentals(new ArrayList<>());
        customer.setBalance(0.0);
        
        // Save customer
        customerRepo.add(customer);
        
        // Audit logging
        auditService.logCustomerAdded(customer);
    }
    
    @Transactional
    public void removeProduct(String productId) {
        Product product = productRepo.findById(productId);
    
        // Validate product can be removed
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        
        // Check if product is currently rented
        if (!product.isAvailable()) {
            List<Rental> activeRentals = rentalRepo.findActiveByProduct(productId);
            if (!activeRentals.isEmpty()) {
                throw new ProductInUseException("Product is currently rented");
            }
        }
        
        // Remove product
        productRepo.remove(productId);
        
        // Audit logging with reason
        auditService.logProductRemoved(product, reason);
    }
    
    @Transactional
    public void rentProduct(String productId, String customerId, int days) {
        Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        // Validate rental
        validateRental(product, customer, days); 
    
        // Calculate charges
        double charge = pricingStrategy.calculatePrice(product, days);
        
        // Create rental
        Rental rental = new Rental(product, customer, days, charge);
        rental.setProduct(product);
        rental.setCustomer(customer);
        rental.setRentedAt(LocalDateTime.now());
        rental.setDueDate(LocalDateTime.now().plusDays(days));
        
        product.setAvailable(false);
        customer.setBalance(customer.getBalance() + charge);
        
        rentalRepo.add(rental);
        productRepo.update(product);
        customerRepo.update(customer);

        return new RentalReceipt(rental, charge);
    }


    private void validateRental(Product product, Customer customer, int days) {
        if (!product.isAvailableForRent()) {
            throw new ProductNotAvailableException("Product is not available for rent");
        }
        
        if (days <= 0) {
            throw new InvalidRentalDurationException("Rental duration must be positive");
        }
        
        if (customer.hasOverdueRentals()) {
            throw new CustomerHasOverdueRentalsException();
        }
        
        validateProductCustomerCompatibility(product, customer);
    }

    private void validateProductCustomerCompatibility(Product product, Customer customer) {
        if (product instanceof Bike) {
            if (!bikeValidationStrategy.isValidRental((Bike) product, customer)) {
                throw new SizeMismatchException();
            }
        }
    }
    
    // Additional query method to find bikes suitable for a customer
    public List<Bike> findSuitableBikes(String customerId) {
        Customer customer = customerRepo.findById(customerId);
        return productRepo.findAvailable().stream()
            .filter(p -> p instanceof Bike)
            .map(p -> (Bike) p)
            .filter(bike -> bike.getSize() == customer.getSize())
            .collect(Collectors.toList());
    }

    @Transactional
    public void chargeCustomer(String customerId, double amount) {
        Customer customer = customerRepo.findById(customerId);
        customer.setBalance(customer.getBalance() + amount);
        customerRepo.update(customer);
    }

    public RentalReport getCurrentRentals() {
        List<RentalSummary> activeRentals = rentalRepo.findActiveRentalsWithDetails();
        Map<String, Long> rentalCounts = rentalRepo.getActiveRentalCountsByType();
        
        return new RentalReport(activeRentals, rentalCounts);
    }

    public OverdueRentalsReport getOverdueRentals() {
        List<OverdueRentalInfo> overdueRentals = rentalRepo.findOverdueRentalsWithDetails();
        return new OverdueRentalsReport(overdueRentals);
    }

    // Calculate price before rental
    public double calculateRentalPrice(String productId, int days) {
        Product product = productRepo.findById(productId);
        return pricingStrategy.calculatePrice(product, days);
    }
}

class RentalReceipt {
    private final String rentalId;
    private final String productId;
    private final String customerId;
    private final double charge;
    private final LocalDateTime rentedAt;
    private final LocalDateTime dueDate;
    
    // Constructor and getters
}

// Add this new class for organized rental reporting
class RentalReport {
    private final List<RentalSummary> rentals;
    private final Map<String, Long> countsByType;
    
    public RentalReport(List<RentalSummary> rentals, Map<String, Long> countsByType) {
        this.rentals = rentals;
        this.countsByType = countsByType;
    }
    
    public List<RentalSummary> getOverdueRentals() {
        return rentals.stream()
            .filter(RentalSummary::isOverdue)
            .collect(Collectors.toList());
    }
    
    public Map<String, Long> getRentalCountsByType() {
        return new HashMap<>(countsByType);
    }
    
    public long getTotalRentedCount() {
        return countsByType.values().stream().mapToLong(Long::valueOf).sum();
    }
    
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder("Current Rental Report\n\n");
        
        // Add summary counts
        report.append("Summary:\n");
        report.append(String.format("Total Rentals: %d\n", getTotalRentedCount()));
        countsByType.forEach((type, count) -> 
            report.append(String.format("%s: %d\n", type, count))
        );
        
        // Add detailed listings
        report.append("\nDetailed Listings:\n");
        rentals.forEach(rental -> 
            report.append(rental.toString()).append("\n\n")
        );
        
        return report.toString();
    }
}

class BikeInventoryReport {
    private final Map<Size, Long> totalCounts;
    private final Map<Size, Long> availableCounts;
    
    public BikeInventoryReport(Map<Size, Long> totalCounts, Map<Size, Long> availableCounts) {
        this.totalCounts = totalCounts;
        this.availableCounts = availableCounts;
    }
    
    public InventoryCount getCountForSize(Size size) {
        long total = totalCounts.getOrDefault(size, 0L);
        long available = availableCounts.getOrDefault(size, 0L);
        return new InventoryCount(total, available);
    }
    
    public Map<Size, InventoryCount> getAllCounts() {
        return Arrays.stream(Size.values())
            .collect(Collectors.toMap(
                size -> size,
                this::getCountForSize
            ));
    }
    
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder("Bike Inventory Report:\n");
        getAllCounts().forEach((size, count) -> 
            report.append(String.format("%s bikes: %s\n", size, count))
        );
        return report.toString();
    }
}

// New report class for overdue rentals
class OverdueRentalsReport {
    private final List<OverdueRentalInfo> overdueRentals;
    
    public OverdueRentalsReport(List<OverdueRentalInfo> overdueRentals) {
        this.overdueRentals = overdueRentals;
    }
    
    public boolean hasOverdueRentals() {
        return !overdueRentals.isEmpty();
    }
    
    public int getOverdueCount() {
        return overdueRentals.size();
    }
    
    public Map<String, List<OverdueRentalInfo>> getOverdueByStatus() {
        return overdueRentals.stream()
            .collect(Collectors.groupingBy(OverdueRentalInfo::getOverdueStatus));
    }
    
    @Override
    public String toString() {
        if (!hasOverdueRentals()) {
            return "No overdue rentals found.";
        }
        
        StringBuilder report = new StringBuilder("OVERDUE RENTALS REPORT\n\n");
        report.append(String.format("Total Overdue: %d\n\n", getOverdueCount()));
        
        Map<String, List<OverdueRentalInfo>> byStatus = getOverdueByStatus();
        
        // Report severely overdue first
        byStatus.getOrDefault("SEVERELY_OVERDUE", Collections.emptyList())
            .forEach(rental -> report.append(rental).append("\n\n"));
            
        byStatus.getOrDefault("MODERATELY_OVERDUE", Collections.emptyList())
            .forEach(rental -> report.append(rental).append("\n\n"));
            
        byStatus.getOrDefault("SLIGHTLY_OVERDUE", Collections.emptyList())
            .forEach(rental -> report.append(rental).append("\n\n"));
        
        return report.toString();
    }
}

// Factory pattern for creating products
class ProductFactory {
    public static Product createBike(Size size, double basePrice) {
        Bike bike = new Bike();
        bike.setSize(size);
        bike.setBasePrice(basePrice);
        bike.setAvailable(true);
        return bike;
    }
    
    public static Product createScooter(ScooterType type, double basePrice) {
        Scooter scooter = new Scooter();
        bike.setType(type);
        bike.setBasePrice(basePrice);
        bike.setAvailable(true);
        return scooter;
    }
}

// Catalog service for product queries
@Service
class ProductCatalogService {
    private final ProductRepository productRepo;
    
    public ProductCatalogService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }
    
    // Get complete catalog of available products
    public ProductCatalog getAvailableProducts() {
        List<Product> products = productRepo.findAvailableForRent();
        Map<String, Long> counts = productRepo.getAvailableProductCounts();
        return new ProductCatalog(products, counts);
    }
    
    // Get available products by type
    public List<Product> getAvailableProductsByType(String productType) {
        return productRepo.findAvailableByType(productType);
    }
    
    // Get available products within price range
    public List<Product> getAvailableProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepo.findAvailableByPriceRange(minPrice, maxPrice);
    }
}

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

// Implementation of the repository
@Repository
class JpaProductRepository implements ProductRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public long countBikesBySize(Size size) {
        return em.createQuery(
            "SELECT COUNT(b) FROM Bike b WHERE b.size = :size", Long.class)
            .setParameter("size", size)
            .getSingleResult();
    }
    
    @Override
    public long countAvailableBikesBySize(Size size) {
        return em.createQuery(
            "SELECT COUNT(b) FROM Bike b WHERE b.size = :size AND b.isAvailable = true", 
            Long.class)
            .setParameter("size", size)
            .getSingleResult();
    }
    
    @Override
    public Map<Size, Long> getBikesCountBySize() {
        List<Object[]> results = em.createQuery(
            "SELECT b.size, COUNT(b) FROM Bike b GROUP BY b.size", 
            Object[].class)
            .getResultList();
            
        return results.stream()
            .collect(Collectors.toMap(
                row -> (Size) row[0],
                row -> (Long) row[1]
            ));
    }
    
    @Override
    public Map<Size, Long> getAvailableBikesCountBySize() {
        List<Object[]> results = em.createQuery(
            "SELECT b.size, COUNT(b) FROM Bike b WHERE b.isAvailable = true GROUP BY b.size", 
            Object[].class)
            .getResultList();
            
        return results.stream()
            .collect(Collectors.toMap(
                row -> (Size) row[0],
                row -> (Long) row[1]
            ));
    }

    @Override
    public List<Product> findAvailableForRent() {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p.isAvailable = true " +
            "AND p.lastMaintenanceDate > :oneMonthAgo", Product.class)
            .setParameter("oneMonthAgo", LocalDateTime.now().minusMonths(1))
            .getResultList();
    }
    
    @Override
    public List<Product> findAvailableByType(String productType) {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p.isAvailable = true " +
            "AND p.productType = :type " +
            "AND p.lastMaintenanceDate > :oneMonthAgo", Product.class)
            .setParameter("type", productType)
            .setParameter("oneMonthAgo", LocalDateTime.now().minusMonths(1))
            .getResultList();
    }
    
    @Override
    public Map<String, Long> getAvailableProductCounts() {
        List<Object[]> results = em.createQuery(
            "SELECT p.productType, COUNT(p) FROM Product p " +
            "WHERE p.isAvailable = true " +
            "AND p.lastMaintenanceDate > :oneMonthAgo " +
            "GROUP BY p.productType", Object[].class)
            .setParameter("oneMonthAgo", LocalDateTime.now().minusMonths(1))
            .getResultList();
            
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }
    
    @Override
    public List<Product> findAvailableByPriceRange(double minPrice, double maxPrice) {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p.isAvailable = true " +
            "AND p.basePrice BETWEEN :minPrice AND :maxPrice " +
            "AND p.lastMaintenanceDate > :oneMonthAgo", Product.class)
            .setParameter("minPrice", minPrice)
            .setParameter("maxPrice", maxPrice)
            .setParameter("oneMonthAgo", LocalDateTime.now().minusMonths(1))
            .getResultList();
    }
    
    // Other repository methods...
}

@RestController
@RequestMapping("/api/inventory")
class InventoryController {
    private final RentalService rentalService;
    
    @GetMapping("/bikes/small/count")
    public InventoryCount getSmallBikesCount() {
        return rentalService.getSmallBikesCount();
    }
    
    @GetMapping("/bikes/{size}/count")
    public InventoryCount getBikesCountBySize(@PathVariable Size size) {
        return rentalService.getBikesCountBySize(size);
    }
    
    @GetMapping("/bikes/report")
    public BikeInventoryReport getFullBikeInventoryReport() {
        return rentalService.getFullBikeInventoryReport();
    }
}

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

@Service
class AuditService {
    public void logProductAdded(Product product) { /* ... */ }
    public void logProductRemoved(Product product, String reason) { /* ... */ }
    public void logRentalCreated(Rental rental) { /* ... */ }
    public void logCustomerCharged(Charge charge) { /* ... */ }
}

@Service
class NotificationService {
    public void sendRentalConfirmation(Rental rental) { /* ... */ }
    public void sendChargeNotification(Charge charge) { /* ... */ }
}