package validation;

public class SizeMatchValidationStrategy implements BikeValidationStrategy {
    @Override
    public boolean isValidRental(Bike bike, Customer customer) {
        return bike.getSize() == customer.getSize();
    }
}

