package uber.LLD.messagequeue.core;

import uber.LLD.messagequeue.client.Message;
import uber.LLD.messagequeue.client.MessageStatus;
import uber.LLD.messagequeue.exception.*;

public interface MessageHandler {
    void handle(Message message) throws MessageProcessingException;
    default void onError(Message message, Exception e) {
        message.setStatus(MessageStatus.FAILED);
    }
}