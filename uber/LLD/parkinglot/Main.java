package uber.LLD.parkinglot;

import uber.LLD.parkinglot.factories.VehicleFactory;
import uber.LLD.parkinglot.managers.ParkingManager;
import uber.LLD.parkinglot.models.Vehicle;
import uber.LLD.parkinglot.observers.DisplayBoard;
import uber.LLD.parkinglot.strategies.NearestSlotStrategy;
import uber.LLD.parkinglot.exceptions.*;

public class Main {
    public static void main(String[] args) {
        // Initialize Parking Manager with 6 slots and Nearest Slot Allocation Strategy
        ParkingManager parkingManager = new ParkingManager(6, new NearestSlotStrategy());

        // Register DisplayBoard as an observer
        DisplayBoard displayBoard = new DisplayBoard();
        parkingManager.registerObserver(displayBoard);

        try {
            // Park Vehicles
            Vehicle vehicle1 = VehicleFactory.createVehicle("KA-01-HH-1234", "White");
            int slot1 = parkingManager.parkVehicle(vehicle1);
            System.out.println("Allocated slot number: " + slot1);

            Vehicle vehicle2 = VehicleFactory.createVehicle("KA-01-HH-9999", "White");
            int slot2 = parkingManager.parkVehicle(vehicle2);
            System.out.println("Allocated slot number: " + slot2);

            Vehicle vehicle3 = VehicleFactory.createVehicle("KA-01-BB-0001", "Black");
            int slot3 = parkingManager.parkVehicle(vehicle3);
            System.out.println("Allocated slot number: " + slot3);

            Vehicle vehicle4 = VehicleFactory.createVehicle("KA-01-HH-7777", "Red");
            int slot4 = parkingManager.parkVehicle(vehicle4);
            System.out.println("Allocated slot number: " + slot4);

            Vehicle vehicle5 = VehicleFactory.createVehicle("KA-01-HH-2701", "Blue");
            int slot5 = parkingManager.parkVehicle(vehicle5);
            System.out.println("Allocated slot number: " + slot5);

            Vehicle vehicle6 = VehicleFactory.createVehicle("KA-01-HH-3141", "Black");
            int slot6 = parkingManager.parkVehicle(vehicle6);
            System.out.println("Allocated slot number: " + slot6);

            // Attempt to park when full
            try {
                Vehicle vehicle7 = VehicleFactory.createVehicle("MH-04-AY-1111", "White");
                parkingManager.parkVehicle(vehicle7);
            } catch (ParkingFullException e) {
                System.out.println(e.getMessage());
            }

            // Display Status
            System.out.println("\nParking Lot Status:");
            parkingManager.displayStatus();

            // Unpark Vehicle from slot 4
            parkingManager.unparkVehicle(4);

            // Display Status after Unparking
            System.out.println("\nParking Lot Status after unparking slot 4:");
            parkingManager.displayStatus();

            // Get Nearest Available Slot
            int nearestSlot = parkingManager.getNearestAvailableSlot();
            System.out.println("\nNearest available slot: " + nearestSlot);

            // Park another vehicle
            Vehicle vehicle7 = VehicleFactory.createVehicle("MH-04-AY-1111", "White");
            int slot7 = parkingManager.parkVehicle(vehicle7);
            System.out.println("Allocated slot number: " + slot7);

            // Retrieve Vehicle Details by Color
            System.out.println("\nRegistration numbers with color White: " + parkingManager.getRegistrationNumbersByColor("White"));
            System.out.println("Slot numbers with color White: " + parkingManager.getSlotNumbersByColor("White"));
            System.out.println("Slot number for vehicle KA-01-HH-3141: " + parkingManager.getSlotNumberByRegistrationNumber("KA-01-HH-3141"));

        } catch (ParkingFullException | SlotAlreadyEmptyException | InvalidSlotException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Operation interrupted");
        }
    }
}

