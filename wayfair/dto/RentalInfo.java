package dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RentalInfo {
    private final String rentalId;
    private final String productId;
    private final String productName;
    private final String productType;
    private final String customerId;
    private final String customerName;
    private final LocalDateTime rentedAt;
    private final LocalDateTime dueDate;

    // Constructor
    public RentalInfo(String rentalId, String productId, String productName, String productType,
                        String customerId, String customerName, LocalDateTime rentedAt, LocalDateTime dueDate) {
        this.rentalId = rentalId;
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.customerId = customerId;
        this.customerName = customerName;
        this.rentedAt = rentedAt;
        this.dueDate = dueDate;
    }

    // Methods
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

// Similar consolidation for other DTOs like OverdueRentalInfo, RentalReceipt, etc.
