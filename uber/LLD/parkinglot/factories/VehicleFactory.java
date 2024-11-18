package uber.LLD.parkinglot.factories;

import uber.LLD.parkinglot.models.Vehicle;

public class VehicleFactory {

    public static Vehicle createVehicle(String registrationNumber, String color) {
        return new Vehicle(registrationNumber, color);
    }
}
