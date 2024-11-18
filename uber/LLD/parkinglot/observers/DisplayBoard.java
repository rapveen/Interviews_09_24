package uber.LLD.parkinglot.observers;

public class DisplayBoard implements Observer {

    @Override
    public void update(String event, int slotNumber) {
        if ("park".equalsIgnoreCase(event)) {
            System.out.println("DisplayBoard: Slot " + slotNumber + " is now occupied.");
        } else if ("unpark".equalsIgnoreCase(event)) {
            System.out.println("DisplayBoard: Slot " + slotNumber + " is now free.");
        }
    }
}
