package uber.LLD.messagequeue.core;

import java.util.concurrent.*;


public class QueueManager {
    private static final QueueManager INSTANCE = new QueueManager();
    private final ConcurrentMap<String, MessageQueue> queues;
    private final ExecutorService executorService;
    
    private QueueManager() {
        this.queues = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
    }
    
    public static QueueManager getInstance() {
        return INSTANCE;
    }
    
    public MessageQueue createQueue(String name) {
        return queues.computeIfAbsent(name, MessageQueue::new);
    }
    
    public void deleteQueue(String name) {
        queues.remove(name);
    }
    
    public MessageQueue getQueue(String name) {
        return queues.get(name);
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
