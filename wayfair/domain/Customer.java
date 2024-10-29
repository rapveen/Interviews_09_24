package domain;

public class Customer {
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
