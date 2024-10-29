package validation;

// Added size validation strategy using Strategy pattern
interface BikeValidationStrategy {
    boolean isValidRental(Bike bike, Customer customer);
}

