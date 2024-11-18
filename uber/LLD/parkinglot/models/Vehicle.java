package uber.LLD.parkinglot.models;


public final class Vehicle {
    private final String registrationNumber;
    private final String color;

    public Vehicle(String registrationNumber, String color) {
        if (registrationNumber == null || registrationNumber.isEmpty()) {
            throw new IllegalArgumentException("Registration Number cannot be null or empty");
        }
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("Color cannot be null or empty");
        }
        this.registrationNumber = registrationNumber;
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Vehicle [Registration Number=" + registrationNumber + ", Color=" + color + "]";
    }
}
