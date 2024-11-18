package uber.LLD.messagequeue.client;

import uber.LLD.messagequeue.core.QueueManager;
import uber.LLD.messagequeue.core.MessageQueue;
import uber.LLD.messagequeue.exception.QueueNotFoundException;

public class Producer {
    private final QueueManager queueManager;
    
    public Producer(QueueManager queueManager) {
        this.queueManager = queueManager;
    }
    
    public void publish(String queueName, String content) {
        MessageQueue queue = queueManager.getQueue(queueName);
        if (queue == null) {
            throw new QueueNotFoundException("Queue not found: " + queueName);
        }
        
        Message message = Message.builder()
            .setContent(content)
            .build();
            
        queue.publish(message);
    }
}