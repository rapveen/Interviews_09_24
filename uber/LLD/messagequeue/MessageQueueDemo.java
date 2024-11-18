package uber.LLD.messagequeue;

import uber.LLD.messagequeue.client.*;
import uber.LLD.messagequeue.core.QueueManager;
import uber.LLD.messagequeue.core.MessageHandler;

import uber.LLD.messagequeue.core.*;
// import uber.LLD.messagequeue.client.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageQueueDemo {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    private static void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] " + message);
    }

    public static void main(String[] args) throws InterruptedException {
        log("Starting Message Queue Demo...");
        
        QueueManager queueManager = QueueManager.getInstance();
        log("Queue Manager initialized");
        
        // Create queue
        String queueName = "test-queue";
        MessageQueue queue = queueManager.createQueue(queueName);
        log("Created queue: " + queueName);
        
        // Add queue listener for monitoring
        queue.addListener(new QueueEventListener() {
            @Override
            public void onMessageAdded(String queueName, Message message) {
                log("EVENT: Message added to " + queueName + " - ID: " + message.getId());
            }
            
            @Override
            public void onMessageConsumed(String queueName, Message message) {
                log("EVENT: Message consumed from " + queueName + " - ID: " + message.getId());
            }
            
            @Override
            public void onError(String queueName, Exception e) {
                log("EVENT: Error in queue " + queueName + " - " + e.getMessage());
            }
        });
        log("Queue listener added");
        
        // Create producer
        Producer producer = new Producer(queueManager);
        log("Producer created");
        
        // Create consumer with custom handler
        MessageHandler handler = message -> {
            log("PROCESSING: Started processing message: '" + message.getContent() + "'");
            // Simulate processing time
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            log("PROCESSING: Completed processing message: '" + message.getContent() + "'");
        };
        
        Consumer consumer = new Consumer(queueManager, handler);
        log("Consumer created");
        
        // Start consumer
        int pollingInterval = 1000; // 1 second
        consumer.start(queueName, pollingInterval);
        log("Consumer started with polling interval: " + pollingInterval + "ms");
        
        // Publish messages
        log("\n--- Publishing Messages ---");
        try {
            producer.publish(queueName, "Hello, World!");
            log("Published message 1: 'Hello, World!'");
            
            Thread.sleep(500); // Wait a bit between messages
            
            producer.publish(queueName, "Another message");
            log("Published message 2: 'Another message'");
            
            Thread.sleep(500);
            
            producer.publish(queueName, "Final test message");
            log("Published message 3: 'Final test message'");
            
        } catch (Exception e) {
            log("ERROR: Failed to publish message - " + e.getMessage());
        }
        
        // Let messages process
        log("\n--- Waiting for message processing (5 seconds) ---");
        Thread.sleep(5000);
        
        // Print final statistics
        log("\n--- Final Statistics ---");
        log("Messages remaining in queue: " + queue.size());
        log("Total listeners: " + queue.getListenerCount());
        
        // Cleanup
        log("\n--- Cleaning up ---");
        consumer.stop();
        log("Consumer stopped");
        
        queueManager.shutdown();
        log("Queue Manager shut down");
        
        log("Demo completed successfully!");
    }
}


// public class MessageQueueDemo {
//     public static void main(String[] args) {
//         System.out.println("1");
//         QueueManager queueManager = QueueManager.getInstance();
//         System.out.println("2");
//         // Create queue
//         queueManager.createQueue("test-queue");
//         System.out.println("3");
//         // Create producer
//         Producer producer = new Producer(queueManager);
//         System.out.println("4");
//         // Create consumer with custom handler
//         MessageHandler handler = message -> {
//             System.out.println("Processing message: " + message.getContent());
//             // Simulate processing
//             try {
//                 Thread.sleep(100);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         };
//         System.out.println("5");
//         Consumer consumer = new Consumer(queueManager, handler);
        
//         // Start consumer
//         consumer.start("test-queue", 1000);
        
//         // Publish messages
//         producer.publish("test-queue", "Hello, World!");
//         producer.publish("test-queue", "Another message");
        
//         // Let messages process
//         try {
//             Thread.sleep(100);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
        
//         // Cleanup
//         consumer.stop();
//         queueManager.shutdown();
//     }
// }

