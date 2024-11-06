/* 
For this if we added TTL for each key and the keys should be evicted when the TTL expired.
There are multiple ways. One is
Lazy Expiration (On-Read):
class CacheEntry {
    Object value;
    long expiryTime;  // currentTimeMillis + TTL

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
Pros:
No overhead for cleanup
Simple implementation
O(1) check during get 
Cons:
Memory waste until key is accessed
Cache size might be inaccurate
Memory pressure in long-running systems



// Dedicated thread scanning keys periodically
class CleanupThread extends Thread {
    private final long cleanupInterval;
    
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            cleanup();
            Thread.sleep(cleanupInterval);
        }
    }
}
Pros:
Automatic cleanup
Configurable intervals
No impact on get/put 
Cons:
Extra CPU overhead
Memory spikes during cleanup
Cleanup might miss short TTLs

Segmented Cleanup (Like Caffeine Cache):

class SegmentedCache {
    List<ConcurrentHashMap<Object, CacheEntry>> segments;
    int currentSegment = 0;
    
    void cleanupSegment() {
        // Clean one segment at a time
        cleanup(segments.get(currentSegment++ % segments.size()));
    }
}
Pros:
Distributed cleanup load
Better memory behavior
More predictable latency 
Cons:
Complex implementation
Extra memory for segments
Coordination overhead
TTL Cache with Efficient Cleanup
Real-world Inspirations:
Redis:
Lazy expiration + periodic sampling
Random sampling for cleanup
Configurable cleanup frequency

Caffeine:
Window TinyLFU for admission
Segmented cleaning
Write buffer optimization

Memcached:
Lazy expiration only
LRU within slab classes
No active cleanup
Best Practices Used in Implementation:


The implemented solution provides:
O(1) get/put operations
Efficient memory usage
Predictable cleanup behavior
Thread-safe operations
Configurable parameters
 
Things to consider,
 Handling Multiple Expiration Entries for the Same Key
Issue: If a key is updated with a new TTL before its previous ExpirationEntry expires, multiple ExpirationEntry instances for the same key may exist in the DelayQueue.
current implementation combines both LRU (size-based) and TTL (time-based) eviction.
allow entries to be refreshed upon access, extending their TTL.

For randomized approach,
Instead of fixed intervals, schedule the cleanup task to execute exactly at the nearest expiration time from the priority queue.
Rescheduling on Updates:
When a new key is added or an existing key's expiration is updated, adjust the scheduler to trigger at the new earliest expiration time.
Efficient Cleanup Execution:
Upon scheduler execution, remove all expired keys and reschedule the next cleanup based on the updated priority queue, ensuring minimal idle runs.

Data Structures Used
ConcurrentHashMap:
Maintains the cache entries in access order, ensuring that the most recently used (MRU) items are easily identifiable.
Facilitates O(1) access and update operations.
Doubly Linked List for LRU Ordering
PriorityQueue (Min-Heap):
Stores cache nodes sorted by their expiration times.
Enables quick retrieval of the next node to expire.
ScheduledExecutorService:
Manages the scheduler that handles the cleanup of expired entries.
Dynamically schedules cleanup tasks based on the nearest expiration time.

Algorithm Used
LRU Management:
Utilize HashMap
Expiration Handling:
Each cache entry (Node) contains an expireTime timestamp.
A PriorityQueue maintains all nodes sorted by their expireTime, allowing efficient identification of the next node to expire.
Dynamic Scheduling:
The scheduler is not fixed to run at regular intervals. Instead, it is dynamically scheduled to execute precisely when the next key is due to expire.
Upon inserting or updating a key, the scheduler adjusts to the new earliest expiration time.
Eviction Strategy:
Expired Entries First: The scheduler ensures that expired entries are removed promptly.
LRU Eviction: If the cache exceeds its capacity after handling expirations, the LRU entry is evicted to maintain the cache size.
But priorityQueue operations take offer() o(logn) and remove o(n) time. so inefficient.

To maintain O(1) time complexity for put(), get(), and remove(), we need to eliminate any operations that are O(log n) or O(n). Heres how we can adjust our implementation:
Use a HashMap to Map Expiration Times to Nodes:
Instead of using a PriorityQueue, we can use an additional HashMap<Long, Set<Node>> called expirationMap that maps expiration times to sets of nodes expiring at that time.
This allows us to insert and remove expiration times in O(1) time.
Maintain a Min-Expiration Time Variable:
Keep track of the earliest expiration time (minExpirationTime) to know when the next key is due to expire.
Update this variable whenever a key is added or removed.
Use a Timer or Scheduler for Cleanup:
Use a single-threaded scheduled executor (ScheduledExecutorService) that schedules the cleanup task to run at minExpirationTime.
This avoids unnecessary CPU usage and eliminates the need for a background thread that continuously sleeps and wakes up.

The use of the syncronized locks cause contention even for read threads as well. 
High contention on a single lock can degrade performance as the number of concurrent threads increases.
Using ReentrantReadWriteLock
Benefits:
Multiple Concurrent Reads: Allows multiple threads to read from the cache simultaneously.
Exclusive Writes: Ensures that write operations are mutually exclusive, preventing data races during modifications.
*/

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
public class LRUCacheWithTTLWithLocks<K, V extends Number> {
    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> lruList;
    private final ConcurrentHashMap<Long, Set<Node<K, V>>> expirationMap;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private long minExpirationTime = Long.MAX_VALUE;
    private ScheduledExecutorService scheduler;
    public LRUCacheWithTTLWithLocks(int capacity) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>();
        this.lruList = new DoublyLinkedList<>();
        this.expirationMap = new ConcurrentHashMap<>();
        startScheduler();
    }
    private void startScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    private void scheduleNextExpiration() {
        rwLock.writeLock().lock();
        try {
            if (scheduler.isShutdown()) {
                return;
            }
            scheduler.shutdownNow(); // Cancel previous tasks
            scheduler = Executors.newSingleThreadScheduledExecutor();
            long delay = minExpirationTime - System.currentTimeMillis();
            if (delay <= 0) {
                delay = 0;
            }
            scheduler.schedule(this::expireKeys, delay, TimeUnit.MILLISECONDS);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    private void expireKeys() {
        rwLock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            while (currentTime >= minExpirationTime) {
                Set<Node<K, V>> nodes = expirationMap.remove(minExpirationTime);
                if (nodes != null) {
                    for (Node<K, V> node : nodes) {
                        cache.remove(node.key);
                        lruList.remove(node);
                    }
                }
                // Update minExpirationTime
                minExpirationTime = expirationMap.keySet().stream()
                        .min(Long::compareTo)
                        .orElse(Long.MAX_VALUE);
                currentTime = System.currentTimeMillis();
            }
            if (minExpirationTime != Long.MAX_VALUE) {
                scheduleNextExpiration();
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    /**
     * Inserts or updates the value for the given key with a specified TTL.
     * If the cache exceeds its capacity, the least recently used (LRU) item is evicted.
     *
     * @param key       The key to insert or update.
     * @param value     The value associated with the key.
     * @param ttlMillis Time-to-live in milliseconds.
     */
    public void put(K key, V value, long ttlMillis) {
        rwLock.writeLock().lock();
        try {
            long expirationTime = System.currentTimeMillis() + ttlMillis;
            Node<K, V> newNode = new Node<>(key, value, expirationTime);
            if (cache.containsKey(key)) {
                Node<K, V> existingNode = cache.get(key);
                lruList.remove(existingNode);
                removeFromExpirationMap(existingNode);
            } else if (cache.size() >= capacity) {
                Node<K, V> lruNode = lruList.removeTail();
                if (lruNode != null) {
                    cache.remove(lruNode.key);
                    removeFromExpirationMap(lruNode);
                }
            }
            cache.put(key, newNode);
            lruList.addToHead(newNode);
            addToExpirationMap(newNode);
            if (expirationTime < minExpirationTime) {
                minExpirationTime = expirationTime;
                scheduleNextExpiration();
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    /**
     * Retrieves the value associated with the given key.
     * Moves the key to the head of the LRU list if found and not expired.
     *
     * @param key The key to retrieve.
     * @return The value if present and not expired; otherwise, null.
     */
    public V get(K key) {
        rwLock.readLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return null;
            }
            if (node.expirationTime <= System.currentTimeMillis()) {
                // Need to modify the cache, upgrade to write lock
                rwLock.readLock().unlock();
                rwLock.writeLock().lock();
                try {
                    cache.remove(key);
                    lruList.remove(node);
                    removeFromExpirationMap(node);
                } finally {
                    // Downgrade to read lock
                    rwLock.writeLock().unlock();
                    rwLock.readLock().lock();
                }
                return null;
            }
            // To move the node to head, need write lock
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();
            try {
                lruList.moveToHead(node);
            } finally {
                rwLock.writeLock().unlock();
                rwLock.readLock().lock();
            }
            return node.value;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    /**
     * Removes the key and its associated value from the cache.
     *
     * @param key The key to remove.
     */
    public void remove(K key) {
        rwLock.writeLock().lock();
        try {
            Node<K, V> node = cache.remove(key);
            if (node != null) {
                lruList.remove(node);
                removeFromExpirationMap(node);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    /**
     * Calculates the average of all non-expired values in the cache.
     *
     * @return The average as a double. Returns 0.0 if there are no non-expired values.
     */
    public double getAverage() {
        rwLock.readLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            double sum = 0.0;
            int count = 0;
            for (Node<K, V> node : cache.values()) {
                if (node.expirationTime > currentTime) {
                    sum += node.value.doubleValue();
                    count++;
                }
            }
            return count == 0 ? 0.0 : (double)sum / count;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    private void addToExpirationMap(Node<K, V> node) {
        expirationMap.computeIfAbsent(node.expirationTime, k -> new HashSet<>()).add(node);
    }
    private void removeFromExpirationMap(Node<K, V> node) {
        Set<Node<K, V>> nodes = expirationMap.get(node.expirationTime);
        if (nodes != null) {
            nodes.remove(node);
            if (nodes.isEmpty()) {
                expirationMap.remove(node.expirationTime);
            }
        }
    }
    /**
     * Shuts down the scheduler gracefully.
     * Should be called when the cache is no longer needed to prevent resource leaks.
     */
    public void shutdown() {
        rwLock.writeLock().lock();
        try {
            scheduler.shutdownNow();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    // Node class for cache entries
    private static class Node<K, V> {
        K key;
        V value;
        long expirationTime;
        Node<K, V> prev;
        Node<K, V> next;
        Node(K key, V value, long expirationTime) {
            this.key = key;
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }
    // Doubly linked list for LRU management
    private static class DoublyLinkedList<K, V> {
        private Node<K, V> head;
        private Node<K, V> tail;
        void addToHead(Node<K, V> node) {
            node.next = head;
            node.prev = null;
            if (head != null) {
                head.prev = node;
            }
            head = node;
            if (tail == null) {
                tail = node;
            }
        }
        void moveToHead(Node<K, V> node) {
            if (node == head) {
                return;
            }
            remove(node);
            addToHead(node);
        }
        void remove(Node<K, V> node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
        }
        Node<K, V> removeTail() {
            if (tail == null) {
                return null;
            }
            Node<K, V> node = tail;
            remove(node);
            return node;
        }
    }
    // For testing purposes
    public static void main(String[] args) throws InterruptedException {
        // Create a cache with capacity 3 and numeric values
        LRUCacheWithTTLWithLocks<String, Double> cache = new LRUCacheWithTTLWithLocks<>(3);
       
        // Insert entries with different TTLs
        cache.put("key1", 10.0, 5000); // Expires in 5 seconds
        cache.put("key2", 20.0, 3000); // Expires in 3 seconds
        cache.put("key3", 30.0, 7000); // Expires in 7 seconds
       
        // Retrieve and print current average
        System.out.println("Average after initial inserts: " + cache.getAverage()); // Should be (10 + 20 + 30) / 3 = 20.0
       
        // Wait for 4 seconds
        Thread.sleep(4000);
       
        // key2 should have expired
        System.out.println("Average after 4 seconds: " + cache.getAverage()); // Should be (10 + 30) / 2 = 20.0
       
        // Insert another entry, causing eviction of LRU (key1)
        cache.put("key4", 40.0, 5000); // Expires in 5 seconds
       
        // key1 was the LRU and should be evicted
        System.out.println("Average after inserting key4: " + cache.getAverage()); // Should be (30 + 40) / 2 = 35.0
       
        // Clean up scheduler
        cache.shutdown();
    }
}


/*
 * For thread-safe TTL expiration of dynamic case,
we ahve to use DelayQueue as PriorityQueue is not thread-safe
DelayQueue: A specialized blocking queue that orders elements based on their delay, inherently suitable for scheduling tasks based on time.

 */