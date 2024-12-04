package cache;

import cache.EvictionPolicy;
import cache.exception.CacheFullException;
import cache.exception.KeyNotFoundException;

/**
 * Cache implementation supporting multiple eviction policies.
 */
public class Cache<K, V> {
    private final int capacity;
    private final EvictionPolicy<K> evictionPolicy;
    private final Map<K, V> cacheMap;

    /**
     * Constructor for Cache.
     *
     * @param capacity       Maximum capacity of the cache.
     * @param evictionPolicy Eviction policy to use.
     */
    public Cache(int capacity, EvictionPolicy<K> evictionPolicy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.evictionPolicy = evictionPolicy;
        this.cacheMap = new HashMap<>(capacity);
    }

    /**
     * Retrieves a value from the cache.
     *
     * @param key The key to retrieve.
     * @return The associated value, or null if not found.
     */
    public V get(K key) {
        if (cacheMap.containsKey(key)) {
            V value = cacheMap.get(key);
            evictionPolicy.keyAccessed(key);
            return value;
        } else {
            return null;
        }
    }

    /**
     * Inserts or updates a key-value pair in the cache.
     *
     * @param key   The key to insert/update.
     * @param value The value to associate with the key.
     */
    public void put(K key, V value) {
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            evictionPolicy.keyAccessed(key);
        } else {
            if (cacheMap.size() >= capacity) {
                K evictKey = evictionPolicy.evictKey();
                if (evictKey != null) {
                    cacheMap.remove(evictKey);
                    evictionPolicy.keyRemoved(evictKey);
                } else {
                    throw new CacheFullException();
                }
            }
            cacheMap.put(key, value);
            evictionPolicy.keyAccessed(key);
        }
    }

    /**
     * Deletes a key-value pair from the cache.
     *
     * @param key The key to delete.
     */
    public void delete(K key) {
        if (cacheMap.containsKey(key)) {
            cacheMap.remove(key);
            evictionPolicy.keyRemoved(key);
        } else {
            throw new KeyNotFoundException(String.valueOf(key));
        }
    }

    /**
     * Clears the entire cache.
     */
    public void clear() {
        cacheMap.clear();
        // Note: Eviction policy may need to reset its internal state if applicable
    }

    /**
     * Returns the current size of the cache.
     *
     * @return The number of key-value pairs in the cache.
     */
    public int size() {
        return cacheMap.size();
    }
}
