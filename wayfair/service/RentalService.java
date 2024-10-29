package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Bike;
import domain.Customer;
import domain.Rental;
import domain.Size;
import dto.BikeInventoryReport;
import dto.RentalInfo;
import pricing.BikeSpecificPricingStrategy;
import pricing.CompositePricingStrategy;
import pricing.EnhancedPricingStrategy;
import pricing.PromotionalPricingStrategy;
import repository.ProductRepository;

// Service layer using Facade pattern
@Service
class RentalService {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final RentalRepository rentalRepo;
    private final PricingStrategy pricingStrategy;
    private final BikeValidationStrategy bikeValidationStrategy;
    private final AuditService auditService;
    private final NotificationService notificationService;
    
    public RentalService(ProductRepository productRepo, 
                        CustomerRepository customerRepo,
                        RentalRepository rentalRepo,
                        PricingStrategy pricingStrategy,
                        BikeValidationStrategy bikeValidationStrategy,
                        AuditService auditService,
                        NotificationService notificationService) {
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
        this.auditService = auditService;
        this.notificationService = notificationService;
    }
    
    // Query methods
    // Specific method to answer "How many small bikes do you have?"
    // public InventoryCount getSmallBikesCount() {
    //     long totalCount = productRepo.countBikesBySize(Size.SMALL);
    //     long availableCount = productRepo.countAvailableBikesBySize(Size.SMALL);
        
    //     return new InventoryCount(totalCount, availableCount);
    // }
    
    // Get inventory count for any bike size
    public InventoryCount getBikesCountBySize(Size size) {
        long totalCount = productRepo.countBikesBySize(size);
        long availableCount = productRepo.countAvailableBikesBySize(size);
        
        return new InventoryCount(totalCount, availableCount);
    }
    
    
    public List<Product> getAvailableProducts() {
        return productRepo.findAvailable();
    }
    
    public double getCustomerBalance(String customerId) {
        return customerRepo.findById(customerId).getBalance()
        .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return customer.getBalance();
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
        return rentalRepo.findCurrentBikeRenters();
    }
    
    // Command methods
    @Transactional
    public void addProduct(Product product) {
            // Validate product details
        validateProduct(product);

        // Save and index product
        productRepo.add(product);
        
        // Audit logging
        auditService.logProductAdded(product);
    }
    
    @Transactional
    public void addCustomer(Customer customer) {
         // Validate customer details
         validateCustomer(customer);
        
        // Save customer
        customerRepo.add(customer);
        
        // Audit logging
        auditService.logCustomerAdded(customer);
    }
    
    @Transactional
    public void removeProduct(String productId) {
        Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
        
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
    public RentalReceipt rentProduct(String productId, String customerId, int days) {
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
        rental.setId(UUID.randomUUID().toString());
        rental.setCustomer(customer);
        rental.setRentedAt(LocalDateTime.now());
        rental.setDueDate(LocalDateTime.now().plusDays(days));
        rental.setReturned(false);
        
        product.setAvailable(false);
        customer.setBalance(customer.getBalance() + charge);
        
        rentalRepo.add(rental);
        productRepo.add(product);
        customerRepo.add(customer);

        // Audit and Notification
        auditService.logRentalCreated(rental);
        notificationService.sendRentalConfirmation(rental);

        return new RentalReceipt(rental.getId(), product.getId(), customer.getId(), charge, rental.getRentedAt(), rental.getDueDate());
    }

    @Transactional
    public void chargeCustomer(String customerId, double amount) {
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));
        customer.setBalance(customer.getBalance() + amount);
        customerRepo.update(customer);
        auditService.logCustomerCharged(customer, amount);
        notificationService.sendChargeNotification(customer, amount);
    }

    // Calculate price before rental
    public double calculateRentalPrice(String productId, int days) {
        Product product = productRepo.findById(productId);
        return pricingStrategy.calculatePrice(product, days);
    }

    private void validateProduct(Product product) {
        if (product.getBasePrice() <= 0) {
            throw new InvalidProductException("Product price must be positive");
        }
        // Additional validations as needed
    }

    private void validateCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new InvalidCustomerException("Customer name is required");
        }
        // Additional validations as needed
    }

    private void validateRental(Product product, Customer customer, int days) {
        if (!product.isAvailableForRent()) {
            throw new ProductNotAvailableException("Product is not available for rent");
        }
        if (days <= 0) {
            throw new InvalidRentalDurationException("Rental duration must be positive");
        }
        if (customer.hasOverdueRentals()) {
            throw new CustomerHasOverdueRentalsException("Customer has overdue rentals");
        }
        if (product instanceof Bike) {
            if (!bikeValidationStrategy.isValidRental((Bike) product, customer)) {
                throw new SizeMismatchException("Bike size does not match customer size");
            }
        }
        // Additional compatibility checks if needed
    }

     // Generic Report for Rentals
     public Report<RentalInfo> getRentalReport() {
        List<RentalInfo> activeRentals = rentalRepo.findActiveRentalsWithDetails();
        Map<String, Long> rentalCounts = rentalRepo.getActiveRentalCountsByType();
        return new Report<>("Current Rental Report", activeRentals, rentalCounts);
    }

    // Generic Report for Overdue Rentals
    public Report<RentalInfo> getOverdueRentalsReport() {
        List<RentalInfo> overdueRentals = rentalRepo.findOverdueRentalsWithDetails();
        Map<String, Long> overdueCounts = overdueRentals.stream()
                .collect(Collectors.groupingBy(RentalInfo::getOverdueStatus, Collectors.counting()));
        return new Report<>("Overdue Rentals Report", overdueRentals, overdueCounts);
    }

    // Generic Report for Bike Inventory
    public Report<BikeInventoryData> getBikeInventoryReport() {
        Map<Size, Long> totalCounts = productRepo.getBikesCountBySize();
        Map<Size, Long> availableCounts = productRepo.getAvailableBikesCountBySize();
        List<BikeInventoryData> bikeData = totalCounts.keySet().stream()
                .map(size -> new BikeInventoryData(size, totalCounts.get(size), availableCounts.getOrDefault(size, 0L)))
                .collect(Collectors.toList());

        Map<String, Long> summary = totalCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name() + " Bikes",
                        Map.Entry::getValue
                ));

        return new Report<>("Bike Inventory Report", bikeData, summary);
    }

    // Inner class to hold bike inventory data
    public static class BikeInventoryData {
        private final Size size;
        private final long totalCount;
        private final long availableCount;

        public BikeInventoryData(Size size, long totalCount, long availableCount) {
            this.size = size;
            this.totalCount = totalCount;
            this.availableCount = availableCount;
        }

        @Override
        public String toString() {
            return String.format("%s Bikes: Total: %d, Available: %d, Rented: %d",
                    size, totalCount, availableCount, totalCount - availableCount);
        }

        // Getters if needed
        // ...
    }
}

