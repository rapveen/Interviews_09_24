package uber.LLD.messagequeue.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import uber.LLD.messagequeue.client.Message;
import uber.LLD.messagequeue.client.MessageStatus;
import java.util.function.Consumer;
import uber.LLD.messagequeue.exception.*;


public class MessageQueue {
    private final String name;
    private final ConcurrentLinkedQueue<Message> messages;
    private final Set<QueueEventListener> listeners;
    private final ReentrantLock consumeLock;
    
    public MessageQueue(String name) {
        this.name = name;
        this.messages = new ConcurrentLinkedQueue<>();
        this.listeners = ConcurrentHashMap.newKeySet();
        this.consumeLock = new ReentrantLock();
    }

    public int size() {
        return messages.size();
    }
    
    public void addListener(QueueEventListener listener) {
        listeners.add(listener);
    }
    
    public void publish(Message message) {
        try {
            messages.offer(message);
            notifyListeners(l -> l.onMessageAdded(name, message));
        } catch (Exception e) {
            notifyListeners(l -> l.onError(name, e));
            throw new MessageQueueException("Failed to publish message", e);
        }
    }
    
    public Message consume() {
        try {
            consumeLock.lock();
            Message message = messages.poll();
            if (message != null) {
                message.setStatus(MessageStatus.PROCESSING);
                notifyListeners(l -> l.onMessageConsumed(name, message));
            }
            return message;
        } finally {
            consumeLock.unlock();
        }
    }
    
    private void notifyListeners(Consumer<QueueEventListener> action) {
        listeners.forEach(listener -> {
            try {
                action.accept(listener);
            } catch (Exception e) {
                // Log error but don't interrupt processing
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        });
    }

    public int getListenerCount() {
        return listeners.size();
    }
}