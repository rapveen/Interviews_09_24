package uber.LLD.messagequeue.core;

import uber.LLD.messagequeue.client.Message;

public interface QueueEventListener {
    void onMessageAdded(String queueName, Message message);
    void onMessageConsumed(String queueName, Message message);
    void onError(String queueName, Exception e);
}