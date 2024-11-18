package uber.LLD.parkinglot.strategies;


import uber.LLD.parkinglot.models.Slot;
import java.util.concurrent.BlockingQueue;

public class NearestSlotStrategy implements SlotAllocationStrategy {

    @Override
    public Slot allocateSlot(BlockingQueue<Slot> availableSlots) throws InterruptedException {
        return availableSlots.take(); // Retrieves and removes the head of the queue, waiting if necessary
    }
}

