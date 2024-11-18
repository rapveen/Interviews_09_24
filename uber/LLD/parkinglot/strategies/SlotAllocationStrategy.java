package uber.LLD.parkinglot.strategies;

import uber.LLD.parkinglot.models.Slot;
import java.util.concurrent.BlockingQueue;

public interface SlotAllocationStrategy {
    Slot allocateSlot(BlockingQueue<Slot> availableSlots) throws InterruptedException;
}
