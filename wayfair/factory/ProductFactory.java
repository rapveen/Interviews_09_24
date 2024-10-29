package factory;

import java.time.LocalDateTime;

import domain.Bike;
import domain.Scooter;
import domain.ScooterType;
import domain.Size;

// Factory pattern for creating products
public class ProductFactory {
    public static Product createBike(String id, String name, Size size, double basePrice, String description) {
        Bike bike = new Bike();
        bike.setId(id);
        bike.setName(name);
        bike.setSize(size);
        bike.setBasePrice(basePrice);
        bike.setDescription(description);
        bike.setAvailable(true);
        bike.setLastMaintenanceDate(LocalDateTime.now());
        return bike;
    }
    
    public static Product createScooter(String id, String name, ScooterType type, double basePrice, String description) {
        Scooter scooter = new Scooter();
        scooter.setId(id);
        scooter.setName(name);
        scooter.setType(type);
        scooter.setBasePrice(basePrice);
        scooter.setDescription(description);
        scooter.setAvailable(true);
        scooter.setLastMaintenanceDate(LocalDateTime.now());
        return scooter;
    }
}
