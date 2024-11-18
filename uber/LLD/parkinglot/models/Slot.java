package uber.LLD.parkinglot.models;


public class Slot implements Comparable<Slot> {
    private final int slotNumber;
    private volatile Vehicle vehicle; // Volatile for visibility in concurrent environments

    public Slot(int slotNumber) {
        if (slotNumber <= 0) {
            throw new IllegalArgumentException("Slot number must be positive");
        }
        this.slotNumber = slotNumber;
        this.vehicle = null;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isAvailable() {
        return vehicle == null;
    }

    public synchronized void parkVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("Slot is already occupied");
        }
        this.vehicle = vehicle;
    }

    public synchronized Vehicle unparkVehicle() {
        if (isAvailable()) {
            throw new IllegalStateException("Slot is already empty");
        }
        Vehicle parkedVehicle = this.vehicle;
        this.vehicle = null;
        return parkedVehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public int compareTo(Slot other) {
        return Integer.compare(this.slotNumber, other.slotNumber);
    }

    @Override
    public String toString() {
        if (isAvailable()) {
            return "Slot " + slotNumber + ": Empty";
        } else {
            return "Slot " + slotNumber + ": Occupied by " + vehicle.toString();
        }
    }
}
