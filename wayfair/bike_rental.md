SOLID Principles:


Single Responsibility: Each class has one specific responsibility
Open/Closed: Product hierarchy is open for extension but closed for modification
Liskov Substitution: Bike and Scooter can be used wherever Product is expected
Interface Segregation: Separate repositories for different entity types
Dependency Inversion: Services depend on abstractions (interfaces) not concrete implementations


Design Patterns:


Template Method: In Product class for price calculation
Strategy: For pricing calculations
Repository: For data access
Factory: For creating products
Facade: RentalService provides a simplified interface to the system


Key Features:


Flexible pricing with discount support
Clear separation of queries and commands
Transactional support for data modifications
Type-safe product hierarchies
Clean domain model

Create a shared Size enum used by both Customer and Bike classes
Added size field to Customer class
Added size validation using Strategy pattern:

New BikeValidationStrategy interface
SizeMatchValidationStrategy implementation to ensure bike size matches customer size

Updated RentalService:

Added size validation in the rentProduct method
Added new findSuitableBikes method to find bikes matching customer size

Added SizeMismatchException for when customers try to rent incorrectly sized bikes



In the current code, while we have a method `getSmallBikesCount()` in the RentalService, we can improve it to be more efficient and explicit. Let me show you a better implementation.


"How many small bikes do you have?" :

1. **Efficient Querying**:
   - Direct database queries using JPA to count bikes by size
   - Separate counts for total and available bikes
   - Optimized SQL queries instead of loading all bikes into memory

2. **Rich Information**:
   - Returns both total count and available count
   - Can distinguish between total bikes, available bikes, and rented bikes
   - Structured response using `InventoryCount` value object

3. **Flexible Reporting**:
   - Can get counts for any bike size
   - Can get a complete inventory report for all sizes
   - Easy to extend for additional inventory metrics

4. **Multiple Access Points**:
   - REST API endpoints for different query needs
   - Structured response format
   - Easy to integrate with any UI or client

To use this to find out how many small bikes you have, you could:

```java
// In your application code
InventoryCount smallBikes = rentalService.getSmallBikesCount();
System.out.println("Small bikes: " + smallBikes);
// Output example: "Small bikes: Total: 10, Available: 7, Rented: 3"

// Or via REST API
GET /api/inventory/bikes/small/count
// Returns: {"totalCount": 10, "availableCount": 7}
```

This implementation:
- Follows SOLID principles (Single Responsibility, Open/Closed)
- Is efficient (uses database-level counting)
- Provides comprehensive information
- Is easy to maintain and extend
- Includes proper error handling and type safety


Let me enhance the code to better answer "What products are there for rent?" by implementing a proper product catalog functionality.


"What products are there for rent?", you can now:

1. **Use the Product Catalog Service**:
```java
ProductCatalogService catalogService = // get service
ProductCatalog catalog = catalogService.getAvailableProducts();
System.out.println(catalog);
```

This will output something like:
```
Available Products for Rent:

Product Counts:
- Bike-SMALL: 5 available
- Bike-MEDIUM: 7 available
- Bike-LARGE: 3 available
- Scooter-ELECTRIC: 4 available
- Scooter-GAS: 2 available

Detailed Listings:
Mountain Bike (Bike-SMALL)
  Price: $25.00
  Size: SMALL, Frame: Aluminum, Weight: 12kg

Electric Scooter (Scooter-ELECTRIC)
  Price: $35.00
  Type: ELECTRIC, Max Speed: 25kph, Range: 30km
...
```

2. **Use the REST API**:
```
GET /api/catalog/available
```

The improvements include:

1. **Comprehensive Product Information**:
   - Detailed product specifications
   - Maintenance status tracking
   - Price information
   - Availability status

2. **Flexible Querying**:
   - Get all available products
   - Filter by product type
   - Filter by price range
   - Get product counts by type

3. **Rich Response Format**:
   - Summary counts
   - Detailed product information
   - Formatted output for easy reading

4. **Maintenance Tracking**:
   - Products are only shown as available if they've had recent maintenance
   - Automatic filtering of products needing maintenance

This implementation:
- Follows SOLID principles
- Provides comprehensive product information
- Is easily extensible
- Includes proper error handling
- Has clean separation of concerns

Looking at the original code I shared, the functionality to check customer balance is already present in the RentalService class:

```java
// From the original RentalService class
public double getCustomerBalance(String customerId) {
    return customerRepo.findById(customerId).getBalance();
}
```

This method works in combination with:

1. The Customer class which tracks balance:
```java
class Customer {
    private String id;
    private String name;
    private Size size;
    private List<Rental> rentals;
    private double balance;
    
    // The balance getter
    public double getBalance() {
        return balance;
    }
}
```

2. The balance gets updated when:
- A product is rented:
```java
@Transactional
public void rentProduct(String productId, String customerId, int days) {
    // ... validation code ...
    
    double charge = pricingStrategy.calculatePrice(product, days);
    customer.setBalance(customer.getBalance() + charge);
    
    // ... rest of rental logic ...
}
```

To check if a customer has a balance, you can simply:
```java
double balance = rentalService.getCustomerBalance("customerId");
boolean oweMoney = balance > 0;
```

The system maintains the balance by:
1. Adding charges when products are rented
2. Storing the balance in the Customer entity
3. Providing a simple way to query the balance

This is a straightforward implementation that directly answers "Does this customer have a balance?" - if the balance is greater than 0, they owe money.

Looking at the original code, we have this functionality in the RentalService class:

```java
public List<Rental> getRentedProducts() {
    return rentalRepo.findActive();
}
```

This works with the RentalRepository interface:
```java
interface RentalRepository {
    void add(Rental rental);
    List<Rental> findByCustomer(String customerId);
    List<Rental> findOverdue();
    List<Rental> findActive();
}
```

However, we could improve this to provide more useful information. Let me suggest some improvements while keeping the existing structure:



Now you can get rented products information in several ways:

```java
// Get full rental report
RentalReport report = rentalService.getCurrentRentals();
System.out.println(report);

// Output would look like:
Current Rental Report

Summary:
Total Rentals: 12
Bike-SMALL: 3
Bike-MEDIUM: 4
Bike-LARGE: 2
Scooter-ELECTRIC: 2
Scooter-GAS: 1

Detailed Listings:
Mountain Bike (Type: Bike-MEDIUM) - Rented by John Doe
  Rented: 2024-10-25
  Due: 2024-10-28
  Status: Active

Electric Scooter (Type: Scooter-ELECTRIC) - Rented by Jane Smith
  Rented: 2024-10-24
  Due: 2024-10-26
  Status: OVERDUE
...
```

The improvements include:

1. **Better Data Organization**:
   - Separate DTO for rental summaries
   - Grouped counts by product type
   - Formatted report output

2. **More Useful Information**:
   - Rental duration
   - Overdue status
   - Customer information
   - Clear formatting

3. **Performance Optimization**:
   - Uses JPA projections to fetch only needed data
   - Single query for counts
   - No N+1 query problems

4. **Flexible Access**:
   - Can get full report
   - Can get just overdue rentals
   - Can get counts by type

These improvements make it easier to:
- Track what's currently rented
- Identify overdue rentals
- See rental patterns by product type
- Get both summary and detailed information


Does this customer have a balance? (aka owe us money)

Looking at the original code and recent improvements, we have this functionality through:

1. In RentalRepository:
```java
interface RentalRepository {
    List<Rental> findOverdue();  // Basic method
}
```

2. In RentalReport (from latest improvements):
```java
class RentalReport {
    public List<RentalSummary> getOverdueRentals() {
        return rentals.stream()
            .filter(RentalSummary::isOverdue)
            .collect(Collectors.toList());
    }
}
```

However, we could improve this functionality to be more specific and useful. Here's the suggested enhancement:


Now you can check overdue rentals like this:

```java
OverdueRentalsReport report = rentalService.getOverdueRentals();
System.out.println(report);

// Output would look like:
OVERDUE RENTALS REPORT

Total Overdue: 3

OVERDUE RENTAL - Mountain Bike (Bike-MEDIUM)
Customer: John Doe
Contact: Email: john@email.com, Phone: 555-0123
Rented: 2024-10-15
Due Date: 2024-10-22
Days Overdue: 7
Status: SEVERELY_OVERDUE

OVERDUE RENTAL - Electric Scooter (Scooter-ELECTRIC)
Customer: Jane Smith
Contact: Email: jane@email.com, Phone: 555-0124
Rented: 2024-10-20
Due Date: 2024-10-25
Days Overdue: 4
Status: MODERATELY_OVERDUE
...
```

The improvements include:

1. **More Detailed Information**:
   - Customer contact details
   - Exact overdue duration
   - Severity classification
   - Full rental history

2. **Better Organization**:
   - Grouped by overdue severity
   - Sorted by due date
   - Clear formatting
   - Summary statistics

3. **Customer Contact Information**:
   - Email and phone included
   - Makes it easy to contact customers about overdue items

4. **Status Classification**:
   - Categorizes overdue rentals by severity
   - Helps prioritize follow-up actions

These improvements make it easier to:
- Quickly identify overdue items
- Contact customers about returns
- Prioritize which overdue rentals need immediate attention
- Track overdue patterns

 **What products are rented? **
Looking at the previous code, we have these implementations to check what products are rented:

1. In RentalService:
```java
public List<Rental> getRentedProducts() {
    return rentalRepo.findActive();
}
```

2. And the improved version with more detailed reporting:
```java
public RentalReport getCurrentRentals() {
    List<RentalSummary> activeRentals = rentalRepo.findActiveRentalsWithDetails();
    Map<String, Long> rentalCounts = rentalRepo.getActiveRentalCountsByType();
    return new RentalReport(activeRentals, rentalCounts);
}
```

3. The RentalReport class that provides organized output:
```java
class RentalReport {
    // ... constructor and fields ...

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
```

This implementation already provides:
1. Summary counts of rented products by type
2. Detailed listings of each rented product
3. Customer information
4. Rental durations and due dates
5. Clear formatting and organization

Using it is straightforward:
```java
RentalReport report = rentalService.getCurrentRentals();
System.out.println(report);

// Output:
Current Rental Report

Summary:
Total Rentals: 12
Bike-SMALL: 3
Bike-MEDIUM: 4
Bike-LARGE: 2
Scooter-ELECTRIC: 2
Scooter-GAS: 1

Detailed Listings:
Mountain Bike (Type: Bike-MEDIUM) - Rented by John Doe
  Rented: 2024-10-25
  Due: 2024-10-28
  Status: Active
...
```

The current implementation seems comprehensive and well-organized. It provides all necessary information about rented products in both summary and detailed format. I don't see any major improvements needed unless there are specific additional requirements you'd like to address.



**Find users which have rented bike?**
The given code provides a method in the RentalService class to find users who have rented bikes:

```java
public List<Customer> getBikeRenters() {
    return rentalRepo.findActive().stream()
        .filter(r -> r.getProduct() instanceof Bike)
        .map(Rental::getCustomer)
        .distinct()
        .collect(Collectors.toList());
}
```

This method does the following:

1. It retrieves all active rentals using `rentalRepo.findActive()`.
2. It filters the rentals to only include those where the rented product is an instance of `Bike` using `.filter(r -> r.getProduct() instanceof Bike)`.
3. It maps each filtered rental to its corresponding customer using `.map(Rental::getCustomer)`.
4. It removes duplicate customers using `.distinct()`.
5. Finally, it collects the resulting customers into a list using `.collect(Collectors.toList())`.

The code looks good and should effectively find all users who have rented bikes. However, there are a couple of potential improvements that could be made:

1. Performance optimization: If the number of active rentals is large, filtering and mapping the entire list could be inefficient. One optimization could be to modify the `RentalRepository` to directly query for active bike rentals and their associated customers. This would reduce the amount of data transferred and processed in the application layer.

2. Null safety: The code assumes that `getProduct()` and `getCustomer()` will always return non-null values. It might be safer to add null checks or use `Optional` to handle potential null values gracefully.

Here's an example of how the repository method could be optimized:

```java
// In RentalRepository
@Query("SELECT DISTINCT r.customer FROM Rental r WHERE r.isReturned = false AND r.product.class = Bike")
List<Customer> findBikeRenters();
```

And the corresponding service method:

```java
// In RentalService
public List<Customer> getBikeRenters() {
    return rentalRepo.findBikeRenters();
}
```

This optimized approach directly queries for the distinct customers associated with active bike rentals, reducing the amount of data processing done in the application layer.

Overall, the existing code is functional and gets the job done. The suggested improvements are optional and depend on the specific performance requirements and data integrity guarantees needed for the application.


7.1. Queries
How many small bikes do you have?

Implemented in ```RentalService.getBikesCountBySize(Size.SMALL)```

What products are there for rent?

Implemented in ```RentalService.getAvailableProducts() and ProductCatalogService.getAvailableProducts()```

Does this customer have a balance? (owe us money)

Implemented in ```RentalService.getCustomerBalance(String customerId)```

What products are rented?

Implemented in ```RentalService.getRentedProducts()```

Are there products that are overdue for return? Who has them?

Implemented in ```RentalService.getOverdueRentals()```

What products has a customer rented?

Implemented in ```RentalService.getCustomerRentals(String customerId)```

Find users who have rented bikes

Implemented in ```RentalService.getBikeRenters()```

Variable Pricing Discussion

Implemented via PricingStrategy interface and its implementations (EnhancedPricingStrategy, SeasonalPricingStrategy, etc.)

7.2. Commands
Add a product to inventory

Implemented in ```RentalService.addProduct(Product product)```

Add a customer

Implemented in ```RentalService.addCustomer(Customer customer)```

Remove a product from inventory (damaged)

Implemented in ```RentalService.removeProduct(String productId, String reason)```

Record that a product is rented to a customer

Implemented in ```RentalService.rentProduct(String productId, String customerId, int days)```

Create a charge for the customer

Implemented in ```RentalService.chargeCustomer(String customerId, double amount)```
