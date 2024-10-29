package dto;

import java.time.LocalDateTime;

class RentalReceipt {
    private final String rentalId;
    private final String productId;
    private final String customerId;
    private final double charge;
    private final LocalDateTime rentedAt;
    private final LocalDateTime dueDate;
    

    public RentalReceipt(String rentalId, String productId, String customerId, double charge,
                        LocalDateTime rentedAt, LocalDateTime dueDate) {
        this.rentalId = rentalId;
        this.productId = productId;
        this.customerId = customerId;
        this.charge = charge;
        this.rentedAt = rentedAt;
        this.dueDate = dueDate;
    }
    
    // Constructor and getters
}