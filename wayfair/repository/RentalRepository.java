package repository;

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
    List<RentalInfo> findActiveRentalsWithDetails();
    
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
