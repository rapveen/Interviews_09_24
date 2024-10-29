package domain;

public class Bike extends Product {
    private Size size;
    
    @Override
    public String getProductType() {
        return "Bike-" + size;
    }

    public Size getSize() {
        return size;
    }
}
