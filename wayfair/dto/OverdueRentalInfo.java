package dto;

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

