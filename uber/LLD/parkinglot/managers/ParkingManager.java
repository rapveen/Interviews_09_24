package uber.LLD.parkinglot.managers;

import uber.LLD.parkinglot.models.Vehicle;
import uber.LLD.parkinglot.models.Slot;
import uber.LLD.parkinglot.observers.Observer;
import uber.LLD.parkinglot.strategies.SlotAllocationStrategy;
import uber.LLD.parkinglot.exceptions.ParkingFullException;
import uber.LLD.parkinglot.exceptions.SlotAlreadyEmptyException;
import uber.LLD.parkinglot.exceptions.InvalidSlotException;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class ParkingManager {
    private final BlockingQueue<Slot> availableSlots;
    private final ConcurrentHashMap<Integer, Vehicle> occupiedSlots;
    private final ConcurrentHashMap<String, Integer> registrationToSlotMap;
    private final ConcurrentHashMap<String, Set<String>> colorToRegistrationsMap;
    private final SlotAllocationStrategy slotAllocationStrategy;
    private final List<Observer> observers;
    private final int totalSlots;

    public ParkingManager(int totalSlots, SlotAllocationStrategy strategy) {
        if (totalSlots <= 0) {
            throw new IllegalArgumentException("Total slots must be positive");
        }
        this.totalSlots = totalSlots;
        this.availableSlots = new PriorityBlockingQueue<>();
        for (int i = 1; i <= totalSlots; i++) {
            availableSlots.add(new Slot(i));
        }
        this.occupiedSlots = new ConcurrentHashMap<>();
        this.registrationToSlotMap = new ConcurrentHashMap<>();
        this.colorToRegistrationsMap = new ConcurrentHashMap<>();
        this.slotAllocationStrategy = strategy;
        this.observers = Collections.synchronizedList(new ArrayList<>());
    }

    // Observer Management
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String event, int slotNumber) {
        List<Observer> snapshot;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }
        for (Observer observer : snapshot) {
            observer.update(event, slotNumber);
        }
    }

    // Park Vehicle
    public int parkVehicle(Vehicle vehicle) throws ParkingFullException, InterruptedException {
        if (availableSlots.isEmpty()) {
            throw new ParkingFullException("Sorry, parking lot is full");
        }
        Slot slot = slotAllocationStrategy.allocateSlot(availableSlots);
        synchronized (slot) {
            if (!slot.isAvailable()) {
                throw new ParkingFullException("Sorry, parking lot is full");
            }
            slot.parkVehicle(vehicle);
        }
        occupiedSlots.put(slot.getSlotNumber(), vehicle);
        registrationToSlotMap.put(vehicle.getRegistrationNumber(), slot.getSlotNumber());
        colorToRegistrationsMap.computeIfAbsent(vehicle.getColor().toLowerCase(), k -> ConcurrentHashMap.newKeySet()).add(vehicle.getRegistrationNumber());
        notifyObservers("park", slot.getSlotNumber());
        return slot.getSlotNumber();
    }

    // Unpark Vehicle
    public void unparkVehicle(int slotNumber) throws SlotAlreadyEmptyException, InvalidSlotException {
        if (slotNumber <= 0 || slotNumber > totalSlots) {
            throw new InvalidSlotException("Invalid slot number: " + slotNumber);
        }
        Vehicle vehicle = occupiedSlots.remove(slotNumber);
        if (vehicle == null) {
            throw new SlotAlreadyEmptyException("Slot " + slotNumber + " is already empty");
        }
        registrationToSlotMap.remove(vehicle.getRegistrationNumber());
        Set<String> regs = colorToRegistrationsMap.get(vehicle.getColor().toLowerCase());
        if (regs != null) {
            regs.remove(vehicle.getRegistrationNumber());
            if (regs.isEmpty()) {
                colorToRegistrationsMap.remove(vehicle.getColor().toLowerCase());
            }
        }
        Slot slot = new Slot(slotNumber);
        availableSlots.add(slot);
        notifyObservers("unpark", slotNumber);
    }

    // Get Nearest Available Slot
    public int getNearestAvailableSlot() {
        Slot slot = availableSlots.peek();
        return slot != null ? slot.getSlotNumber() : -1;
    }

    // Get Registration Numbers by Color
    public List<String> getRegistrationNumbersByColor(String color) {
        Set<String> regs = colorToRegistrationsMap.get(color.toLowerCase());
        if (regs == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(regs);
    }

    // Get Slot Numbers by Color
    public List<Integer> getSlotNumbersByColor(String color) {
        Set<String> regs = colorToRegistrationsMap.get(color.toLowerCase());
        if (regs == null) {
            return Collections.emptyList();
        }
        List<Integer> slots = new ArrayList<>();
        for (String reg : regs) {
            Integer slotNumber = registrationToSlotMap.get(reg);
            if (slotNumber != null) {
                slots.add(slotNumber);
            }
        }
        Collections.sort(slots);
        return slots;
    }

    // Get Slot Number by Registration Number
    public int getSlotNumberByRegistrationNumber(String registrationNumber) {
        Integer slotNumber = registrationToSlotMap.get(registrationNumber);
        return slotNumber != null ? slotNumber : -1;
    }

    // Display Status
    public void displayStatus() {
        System.out.println("Slot No.\tRegistration No\tColor");
        occupiedSlots.keySet().stream().sorted().forEach(slotNumber -> {
            Vehicle vehicle = occupiedSlots.get(slotNumber);
            System.out.println(slotNumber + "\t\t" + vehicle.getRegistrationNumber() + "\t\t" + vehicle.getColor());
        });
    }
}
