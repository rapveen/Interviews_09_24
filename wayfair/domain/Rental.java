package domain;


import java.time.LocalDateTime;

public class Rental {
    private String id;
    private Product product;
    private Customer customer;
    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;
    private boolean isReturned;
    
    Rental() {

    }

    public Rental(String id, Product product, Customer customer, LocalDateTime rentedAt, LocalDateTime dueDate) {
        this.id = id;
        this.product = product;
        this.customer = customer;
        this.rentedAt = rentedAt;
        this.dueDate = dueDate;
        this.isReturned = false;
    }
    // Getters and setters
}

