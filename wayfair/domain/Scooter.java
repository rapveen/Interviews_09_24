package domain;

public class Scooter extends Product {
    private ScooterType type;
    
    @Override
    public String getProductType() {
        return "Scooter-" + type;
    }
}

