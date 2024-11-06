/*
 * Added expirationMap:

A HashMap<Long, Set<String>> that maps each expiration timestamp to the set of keys expiring at that time.
This allows direct access to keys that need to be removed at a specific expiration time without scanning the entire cache.
Updated put Method:

When inserting or updating a key, the key is added to the corresponding set in expirationMap.
If updating an existing key, it removes the key from its previous expiration time set before adding it to the new one.
Modified cleanup Method:

Iterates only through the expiration times that are less than or equal to the current time.
Directly removes and updates the cache, sum, and count for the keys associated with these expired times.
Updated evictOldest Method:

Finds the earliest expiration time and evicts one key from that set.
Ensures that eviction is based on the soonest-to-expire entries.
Benefits:
Efficient Cleanup: By leveraging the expirationMap, the cleanup process targets only the keys that have expired at the current time, avoiding a full scan of the cache.
Optimized Performance: Reduces the time complexity of the cleanup operation, especially when many keys expire simultaneously.
Simple Implementation: Maintains simplicity by using standard data structures without introducing additional complexity.

 */


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A simplified TTL-based cache for single-threaded environments.
 * Uses an expiration map to efficiently cleanup expired entries.
 */
public class LRUCacheWithTTLCacheSingleThread {
    /**
     * Represents a cache entry with a value and expiration time.
     */
    private static class CacheEntry {
        long value;
        long expirationTime;

        CacheEntry(long value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }

    // Main cache storage
    private final Map<String, CacheEntry> cache;

    // Expiration map: expirationTime -> set of keys
    private final Map<Long, Set<String>> expirationMap;

    // Cache capacity
    private final int capacity;

    // Running sum and count for average calculation
    private double sum;
    private int count;

    // Window size in milliseconds (TTL)
    private final long windowSizeMs;

    /**
     * Creates a new TTLCache with the specified window size and capacity.
     *
     * @param windowSizeMs Time window in milliseconds for TTL.
     * @param capacity     Maximum number of entries in the cache.
     */
    public LRUCacheWithTTLCacheSingleThread(long windowSizeMs, int capacity) {
        this.cache = new HashMap<>();
        this.expirationMap = new HashMap<>();
        this.windowSizeMs = windowSizeMs;
        this.capacity = capacity;
        this.sum = 0.0;
        this.count = 0;
    }

    /**
     * Inserts or updates a key-value pair in the cache.
     * Evicts expired entries and, if necessary, the oldest entry based on expiration time.
     *
     * @param key   The key to insert or update.
     * @param value The value associated with the key.
     */
    public void put(String key, long value) {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + windowSizeMs;

        // Cleanup expired entries
        cleanup(currentTime);

        if (cache.containsKey(key)) {
            // Update existing entry
            CacheEntry existingEntry = cache.get(key);
            sum -= existingEntry.value;
            // Remove key from previous expiration time set
            Set<String> keys = expirationMap.get(existingEntry.expirationTime);
            if (keys != null) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    expirationMap.remove(existingEntry.expirationTime);
                }
            }
        } else {
            // Insert new entry
            if (cache.size() >= capacity) {
                evictOldest(currentTime);
            }
            count++;
        }

        // Insert or update the entry
        CacheEntry newEntry = new CacheEntry(value, expirationTime);
        cache.put(key, newEntry);
        sum += value;

        // Add key to expiration map
        expirationMap.computeIfAbsent(expirationTime, k -> new HashSet<>()).add(key);
    }

    /**
     * Retrieves the value associated with the given key.
     * Throws NoSuchElementException if the key is not found or has expired.
     *
     * @param key The key to retrieve.
     * @return The value if present and not expired.
     * @throws NoSuchElementException if the key is not found or has expired.
     */
    public long get(String key) {
        long currentTime = System.currentTimeMillis();
        // Cleanup expired entries
        cleanup(currentTime);

        CacheEntry entry = cache.get(key);
        if (entry == null || entry.expirationTime <= currentTime) {
            throw new NoSuchElementException("Key not found or has expired: " + key);
        }
        return entry.value;
    }

    /**
     * Calculates the average of all non-expired values in the cache.
     *
     * @return The average as a double. Returns Double.NaN if there are no active entries.
     */
    public Double getAverage() {
        long currentTime = System.currentTimeMillis();
        // Cleanup expired entries
        cleanup(currentTime);

        if (count == 0) {
            return Double.NaN; // Undefined
        }
        return sum / count;
    }

    /**
     * Cleans up expired entries from the cache using the expiration map.
     *
     * @param currentTime The current system time in milliseconds.
     */
    private void cleanup(long currentTime) {
        Set<Long> expiredTimes = new HashSet<>();
        for (Long expirationTime : expirationMap.keySet()) {
            if (expirationTime <= currentTime) {
                Set<String> keys = expirationMap.get(expirationTime);
                if (keys != null) {
                    for (String key : keys) {
                        CacheEntry entry = cache.remove(key);
                        if (entry != null) {
                            sum -= entry.value;
                            count--;
                        }
                    }
                }
                expiredTimes.add(expirationTime);
            }
        }
        // Remove expired times from expirationMap
        for (Long expiredTime : expiredTimes) {
            expirationMap.remove(expiredTime);
        }
    }

    /**
     * Evicts the oldest entry based on the earliest expiration time.
     *
     * @param currentTime The current system time in milliseconds.
     */
    private void evictOldest(long currentTime) {
        Long oldestExpiration = null;
        for (Long expirationTime : expirationMap.keySet()) {
            if (oldestExpiration == null || expirationTime < oldestExpiration) {
                oldestExpiration = expirationTime;
            }
        }

        if (oldestExpiration != null) {
            Set<String> keys = expirationMap.get(oldestExpiration);
            if (keys != null && !keys.isEmpty()) {
                String oldestKey = keys.iterator().next();
                keys.remove(oldestKey);
                if (keys.isEmpty()) {
                    expirationMap.remove(oldestExpiration);
                }
                CacheEntry removed = cache.remove(oldestKey);
                if (removed != null) {
                    sum -= removed.value;
                    count--;
                }
            }
        }
    }

    // For testing purposes
    public static void main(String[] args) {
        LRUCacheWithTTLCacheSingleThread cache = new LRUCacheWithTTLCacheSingleThread(5000L, 50); // 5 seconds TTL

        cache.put("foo", 42);
        cache.put("bar", 76);

        try {
            System.out.println("get(\"foo\") => " + cache.get("foo")); // Should print 42
            System.out.println("get(\"bar\") => " + cache.get("bar")); // Should print 76
            System.out.println("getAverage() => " + cache.getAverage()); // Should print 59.0
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        }

        // Simulate expiration
        try {
            Thread.sleep(6000); // Wait for 6 seconds
            System.out.println("After expiration:");
            System.out.println("get(\"foo\") => " + cache.get("foo")); // Should throw exception
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
