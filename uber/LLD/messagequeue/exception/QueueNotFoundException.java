package uber.LLD.messagequeue.exception;

public class QueueNotFoundException extends MessageQueueException {
    public QueueNotFoundException(String message) {
        super(message);
    }
}
