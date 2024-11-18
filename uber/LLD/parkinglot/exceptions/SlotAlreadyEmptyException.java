package uber.LLD.parkinglot.exceptions;

public class SlotAlreadyEmptyException extends Exception {
    public SlotAlreadyEmptyException(String message) {
        super(message);
    }
}