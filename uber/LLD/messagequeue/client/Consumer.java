package uber.LLD.messagequeue.client;

import uber.LLD.messagequeue.core.QueueManager;
import uber.LLD.messagequeue.core.MessageQueue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import uber.LLD.messagequeue.core.MessageHandler;
import uber.LLD.messagequeue.exception.QueueNotFoundException;

public class Consumer {
    private final QueueManager queueManager;
    private final MessageHandler handler;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running;
    
    public Consumer(QueueManager queueManager, MessageHandler handler) {
        this.queueManager = queueManager;
        this.handler = handler;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void start(String queueName, long pollingIntervalMs) {
        running = true;
        scheduler.scheduleAtFixedRate(() -> {
            if (!running) return;
            
            try {
                MessageQueue queue = queueManager.getQueue(queueName);
                if (queue == null) {
                    throw new QueueNotFoundException("Queue not found: " + queueName);
                }
                
                Message message = queue.consume();
                if (message != null) {
                    processMessage(message);
                }
            } catch (Exception e) {
                // Log error but continue processing
                System.err.println("Error processing message: " + e.getMessage());
            }
        }, 0, pollingIntervalMs, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private void processMessage(Message message) {
        try {
            handler.handle(message);
            message.setStatus(MessageStatus.ACKNOWLEDGED);
        } catch (Exception e) {
            handler.onError(message, e);
        }
    }
}